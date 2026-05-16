package com.example.interiordesign_ai.network

import com.example.interiordesign_ai.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────────────
    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<User>>

    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<User>>

    // ── User Profile ─────────────────────────────────────────────────────────
    @GET("user/get_profile.php")
    suspend fun getProfile(@Query("user_id") userId: Int): Response<ApiResponse<User>>

    @POST("user/update_profile.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<User>>

    // ── Designs ───────────────────────────────────────────────────────────────
    @POST("designs/save_design.php")
    suspend fun saveDesign(@Body request: SaveDesignRequest): Response<ApiResponse<DesignIdData>>

    @GET("designs/get_designs.php")
    suspend fun getDesigns(
        @Query("user_id")    userId: Int,
        @Query("saved_only") savedOnly: Int = 0,
        @Query("limit")      limit: Int = 50
    ): Response<ApiResponse<List<Design>>>

    @POST("designs/delete_design.php")
    suspend fun deleteDesign(@Body request: DeleteDesignRequest): Response<ApiResponse<Unit>>

    @POST("designs/toggle_save.php")
    suspend fun toggleSave(@Body request: ToggleSaveRequest): Response<ApiResponse<ToggleSaveData>>

    // ── Image Upload ──────────────────────────────────────────────────────────
    @Multipart
    @POST("uploads/upload_image.php")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ApiResponse<UploadImageData>>

    // ── Notifications ─────────────────────────────────────────────────────────
    @GET("notifications/get_notifications.php")
    suspend fun getNotifications(@Query("user_id") userId: Int): Response<ApiResponse<NotificationsData>>

    @POST("notifications/mark_read.php")
    suspend fun markRead(@Body request: MarkReadRequest): Response<ApiResponse<Unit>>

    // ── Budget ────────────────────────────────────────────────────────────────
    @POST("budget/save_estimate.php")
    suspend fun saveEstimate(@Body request: SaveEstimateRequest): Response<ApiResponse<Unit>>

    // ── AI Image Generation (PHP proxy → Cloudflare) ──────────────────────────
    @POST("ai/generate_image.php")
    suspend fun generateAiImage(@Body request: AiGenerateRequest): Response<ApiResponse<AiImageData>>
}
