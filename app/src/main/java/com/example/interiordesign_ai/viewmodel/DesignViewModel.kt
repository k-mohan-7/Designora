package com.example.interiordesign_ai.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interiordesign_ai.model.*
import com.example.interiordesign_ai.network.RetrofitClient
import com.example.interiordesign_ai.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

sealed class DesignOpState {
    object Idle    : DesignOpState()
    object Loading : DesignOpState()
    data class Success(val message: String = "") : DesignOpState()
    data class Error(val message: String) : DesignOpState()
}

class DesignViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionManager(app)
    private val api = RetrofitClient.api

    private val _designs = MutableStateFlow<List<Design>>(emptyList())
    val designs: StateFlow<List<Design>> = _designs.asStateFlow()

    private val _recentDesigns = MutableStateFlow<List<Design>>(emptyList())
    val recentDesigns: StateFlow<List<Design>> = _recentDesigns.asStateFlow()

    private val _opState = MutableStateFlow<DesignOpState>(DesignOpState.Idle)
    val opState: StateFlow<DesignOpState> = _opState.asStateFlow()

    private val _generatedImageUrl = MutableStateFlow<String?>(null)
    val generatedImageUrl: StateFlow<String?> = _generatedImageUrl.asStateFlow()

    fun resetOpState() { _opState.value = DesignOpState.Idle }
    fun clearGeneratedImage() { _generatedImageUrl.value = null }

    // ── Load designs ─────────────────────────────────────────────────────────

    fun loadDesigns(savedOnly: Boolean = false) {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val response = api.getDesigns(userId, if (savedOnly) 1 else 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    _designs.value = response.body()!!.data ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    fun loadRecentDesigns() {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val response = api.getDesigns(userId, 0, 2)
                if (response.isSuccessful && response.body()?.success == true) {
                    _recentDesigns.value = response.body()!!.data ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    // ── Upload flow: room photo → img2img AI → save ─────────────────────────

    fun generateAndSaveDesign(
        context: Context,
        imageUri: Uri?,
        title: String,
        area: String,
        budget: String,
        colorPref: String,
        furnitureType: String,
        designStyle: String,
        lighting: String,
        description: String = ""
    ) {
        _opState.value = DesignOpState.Loading
        viewModelScope.launch {
            try {
                val userId = session.getUserId()
                if (userId == 0) {
                    _opState.value = DesignOpState.Error("Session expired. Please login again.")
                    return@launch
                }

                if (imageUri == null) {
                    _opState.value = DesignOpState.Error("Please upload a room photo first.")
                    return@launch
                }

                // 1. Upload original room photo to server for storage
                var originalUrl = ""
                val file = uriToFile(context, imageUri)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val uploadResp = api.uploadImage(part)
                if (uploadResp.isSuccessful && uploadResp.body()?.success == true) {
                    originalUrl = uploadResp.body()!!.data!!.url
                }

                // 2. Encode the room photo as base64 for img2img
                val imageBase64 = withContext(Dispatchers.IO) {
                    imageUriToBase64(context, imageUri)
                }

                // 3. Build a detailed prompt from the user's parameters
                val prompt = buildImg2ImgPrompt(
                    area, budget, colorPref, furnitureType,
                    designStyle, lighting, description
                )

                // 4. Call Cloudflare img2img via secure PHP proxy
                val aiResp = api.generateAiImage(
                    AiGenerateRequest(
                        prompt = prompt,
                        userId = userId,
                        image  = imageBase64,
                        mode   = "img2img"
                    )
                )
                if (!aiResp.isSuccessful || aiResp.body()?.success != true) {
                    val msg = aiResp.body()?.message ?: "AI generation failed (HTTP ${aiResp.code()})"
                    _opState.value = DesignOpState.Error(msg)
                    return@launch
                }

                val generatedUrl = aiResp.body()!!.data!!.imageUrl
                _generatedImageUrl.value = generatedUrl

                // 5. Save design record
                val req = SaveDesignRequest(
                    userId = userId,
                    title = title.ifBlank { "$designStyle Room Design" },
                    originalImageUrl = originalUrl,
                    generatedImageUrl = generatedUrl,
                    prompt = prompt,
                    area = area,
                    budget = budget,
                    colorPref = colorPref,
                    furnitureType = furnitureType,
                    designStyle = designStyle,
                    lighting = lighting,
                    isSaved = 1,
                    source = "upload"
                )
                val saveResp = api.saveDesign(req)
                if (saveResp.isSuccessful && saveResp.body()?.success == true) {
                    loadRecentDesigns()
                    _opState.value = DesignOpState.Success("Design generated and saved!")
                } else {
                    _opState.value = DesignOpState.Error(
                        saveResp.body()?.message ?: "Failed to save design"
                    )
                }
            } catch (e: java.net.SocketTimeoutException) {
                _opState.value = DesignOpState.Error("AI generation timed out. Please try again.")
            } catch (e: java.io.IOException) {
                _opState.value = DesignOpState.Error("Network error. Check your connection and try again.")
            } catch (e: Exception) {
                _opState.value = DesignOpState.Error("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    // ── Design-assistant flow: text prompt → text2img (no room photo) ────────

    fun generateFromPrompt(prompt: String) {
        _opState.value = DesignOpState.Loading
        viewModelScope.launch {
            try {
                val userId = session.getUserId()
                if (userId == 0) {
                    _opState.value = DesignOpState.Error("Session expired. Please login again.")
                    return@launch
                }

                val aiResp = api.generateAiImage(
                    AiGenerateRequest(
                        prompt = prompt,
                        userId = userId,
                        image  = null,
                        mode   = "text2img"
                    )
                )
                if (!aiResp.isSuccessful || aiResp.body()?.success != true) {
                    val msg = aiResp.body()?.message ?: "AI generation failed (HTTP ${aiResp.code()})"
                    _opState.value = DesignOpState.Error(msg)
                    return@launch
                }

                val generatedUrl = aiResp.body()!!.data!!.imageUrl
                _generatedImageUrl.value = generatedUrl
                _opState.value = DesignOpState.Success("Design generated!")
            } catch (e: java.net.SocketTimeoutException) {
                _opState.value = DesignOpState.Error("AI generation timed out. Please try again.")
            } catch (e: java.io.IOException) {
                _opState.value = DesignOpState.Error("Network error. Check your connection and try again.")
            } catch (e: Exception) {
                _opState.value = DesignOpState.Error("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    // ── Save from assistant (explicit user action) ───────────────────────────

    fun saveDesignFromAssistant(generatedUrl: String, prompt: String) {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val req = SaveDesignRequest(
                    userId = userId,
                    title = "Assistant Design",
                    originalImageUrl = "",
                    generatedImageUrl = generatedUrl,
                    prompt = prompt,
                    area = "", budget = "", colorPref = "",
                    furnitureType = "", designStyle = "", lighting = "",
                    isSaved = 1, source = "assistant"
                )
                api.saveDesign(req)
                loadDesigns(savedOnly = false)
                loadRecentDesigns()
                _opState.value = DesignOpState.Success("Saved to My Designs!")
            } catch (_: Exception) {}
        }
    }

    // ── Delete / Toggle ─────────────────────────────────────────────────────

    fun deleteDesign(designId: Int) {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val response = api.deleteDesign(DeleteDesignRequest(designId, userId))
                if (response.isSuccessful && response.body()?.success == true) {
                    _designs.value = _designs.value.filter { it.id != designId }
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleSave(designId: Int) {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val response = api.toggleSave(ToggleSaveRequest(designId, userId))
                if (response.isSuccessful && response.body()?.success == true) {
                    val newState = response.body()!!.data!!.isSaved
                    _designs.value = _designs.value.map {
                        if (it.id == designId) it.copy(isSaved = newState) else it
                    }
                }
            } catch (_: Exception) {}
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Build a detailed prompt for the img2img model.
     * Describes what the redesigned room should look like.
     */
    private fun buildImg2ImgPrompt(
        area: String, budget: String, colorPref: String,
        furniture: String, style: String, lighting: String,
        description: String
    ): String {
        val parts = mutableListOf<String>()
        parts.add("Redesign this room as a professional photorealistic interior")
        if (style.isNotBlank())     parts.add("in $style style")
        if (area.isNotBlank())      parts.add("optimized for $area sq.ft space")
        if (colorPref.isNotBlank()) parts.add("using $colorPref color scheme on walls and decor")
        if (furniture.isNotBlank()) parts.add("furnished with $furniture")
        if (lighting.isNotBlank())  parts.add("with $lighting")
        if (budget.isNotBlank())    parts.add("$budget budget tier finish quality")
        if (description.isNotBlank()) parts.add(description)
        parts.add("high quality, 4K, realistic interior photograph, beautiful composition")
        return parts.joinToString(", ") + "."
    }

    /** Encode a content URI as base64 JPEG, resized to max 512px for the AI model. */
    private fun imageUriToBase64(context: Context, uri: Uri): String {
        val input = context.contentResolver.openInputStream(uri)!!
        val original = BitmapFactory.decodeStream(input)
        input.close()

        // Resize keeping aspect ratio, max 512px on longest edge
        val maxSide = 512
        val scale = minOf(maxSide.toFloat() / original.width, maxSide.toFloat() / original.height)
        val w = (original.width * scale).toInt()
        val h = (original.height * scale).toInt()
        val resized = Bitmap.createScaledBitmap(original, w, h, true)
        if (resized !== original) original.recycle()

        val baos = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.PNG, 100, baos)
        resized.recycle()

        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
        return tempFile
    }
}
