package com.example.interiordesign_ai.model

import com.google.gson.annotations.SerializedName

// Generic API wrapper
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: T?,
    @SerializedName("message") val message: String
)

// User
data class User(
    @SerializedName("id")            val id: Int = 0,
    @SerializedName("name")          val name: String = "",
    @SerializedName("phone")         val phone: String = "",
    @SerializedName("email")         val email: String = "",
    @SerializedName("profile_image") val profileImage: String = "",
    @SerializedName("address")       val address: String = "",
    @SerializedName("location")      val location: String = "",
    @SerializedName("gender")        val gender: String = "",
    @SerializedName("dob")           val dob: String = "",
    @SerializedName("created_at")    val createdAt: String = ""
)

// Auth register request
data class RegisterRequest(
    @SerializedName("name")     val name: String,
    @SerializedName("phone")    val phone: String,
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

// Auth login request
data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

// Design
data class Design(
    @SerializedName("id")                   val id: Int = 0,
    @SerializedName("user_id")              val userId: Int = 0,
    @SerializedName("title")                val title: String = "My Design",
    @SerializedName("original_image_url")   val originalImageUrl: String = "",
    @SerializedName("generated_image_url")  val generatedImageUrl: String = "",
    @SerializedName("prompt")               val prompt: String = "",
    @SerializedName("area")                 val area: String = "",
    @SerializedName("budget")               val budget: String = "",
    @SerializedName("color_pref")           val colorPref: String = "",
    @SerializedName("furniture_type")       val furnitureType: String = "",
    @SerializedName("design_style")         val designStyle: String = "",
    @SerializedName("lighting")             val lighting: String = "",
    @SerializedName("is_saved")             val isSaved: Int = 0,
    @SerializedName("source")              val source: String = "upload",
    @SerializedName("created_at")           val createdAt: String = ""
)

// Save design request
data class SaveDesignRequest(
    @SerializedName("user_id")              val userId: Int,
    @SerializedName("title")                val title: String,
    @SerializedName("original_image_url")   val originalImageUrl: String,
    @SerializedName("generated_image_url")  val generatedImageUrl: String,
    @SerializedName("prompt")               val prompt: String,
    @SerializedName("area")                 val area: String,
    @SerializedName("budget")              val budget: String,
    @SerializedName("color_pref")           val colorPref: String,
    @SerializedName("furniture_type")       val furnitureType: String,
    @SerializedName("design_style")         val designStyle: String,
    @SerializedName("lighting")             val lighting: String,
    @SerializedName("is_saved")             val isSaved: Int,
    @SerializedName("source")              val source: String
)

// Toggle save request
data class ToggleSaveRequest(
    @SerializedName("design_id") val designId: Int,
    @SerializedName("user_id")   val userId: Int
)

// Delete design request
data class DeleteDesignRequest(
    @SerializedName("design_id") val designId: Int,
    @SerializedName("user_id")   val userId: Int
)

// DesignIdResponse (for save_design)
data class DesignIdData(
    @SerializedName("design_id") val designId: Int
)

// Upload image response
data class UploadImageData(
    @SerializedName("url") val url: String
)

// Notification
data class AppNotification(
    @SerializedName("id")         val id: Int = 0,
    @SerializedName("user_id")    val userId: Int = 0,
    @SerializedName("title")      val title: String = "",
    @SerializedName("message")    val message: String = "",
    @SerializedName("type")       val type: String = "system",
    @SerializedName("is_read")    val isRead: Int = 0,
    @SerializedName("created_at") val createdAt: String = ""
)

// Notifications response data
data class NotificationsData(
    @SerializedName("notifications") val notifications: List<AppNotification>,
    @SerializedName("unread_count")  val unreadCount: Int
)

// Mark read request
data class MarkReadRequest(
    @SerializedName("user_id")         val userId: Int,
    @SerializedName("notification_id") val notificationId: Int = 0 // 0 = all
)

// Budget estimate request
data class SaveEstimateRequest(
    @SerializedName("user_id")       val userId: Int,
    @SerializedName("state")         val state: String,
    @SerializedName("city")          val city: String,
    @SerializedName("district")      val district: String,
    @SerializedName("room_type")     val roomType: String,
    @SerializedName("area_sqft")     val areaSqft: Double,
    @SerializedName("quality_tier")  val qualityTier: String,
    @SerializedName("total_cost")    val totalCost: Double,
    @SerializedName("breakdown_json")val breakdownJson: String
)

// Update profile request
data class UpdateProfileRequest(
    @SerializedName("user_id")  val userId: Int,
    @SerializedName("name")     val name: String,
    @SerializedName("phone")    val phone: String,
    @SerializedName("email")    val email: String,
    @SerializedName("address")  val address: String,
    @SerializedName("location") val location: String,
    @SerializedName("gender")   val gender: String,
    @SerializedName("dob")      val dob: String
)

// Toggle save response
data class ToggleSaveData(
    @SerializedName("is_saved") val isSaved: Int
)

// ── AI Image Generation ──────────────────────────────────────────────────────
data class AiGenerateRequest(
    @SerializedName("prompt")  val prompt: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("image")   val image: String? = null,   // base64 for img2img mode
    @SerializedName("mode")    val mode: String = "text2img" // "img2img" or "text2img"
)

data class AiImageData(
    @SerializedName("image_url")    val imageUrl: String = "",
    @SerializedName("image_base64") val imageBase64: String = ""
)
