package com.SIMATS.nutriai.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// --- Models ---
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class HealthProfileRequest(
    val user_id: Int,
    val age: Int,
    val gender: String,
    val height_cm: Double,
    val weight_kg: Double,
    val activity_level: String,
    val sleep_hours: Double,
    val stress_level: String
)

data class ConditionRequest(
    val user_id: Int,
    val condition_id: Int
)

data class SaveConditionRequest(
    val user_id: Int,
    val disease: String,
    val stage: String
)

data class NutritionLimitRequest(
    val user_id: Int,
    val max_sugar: Double,
    val max_sodium: Double,
    val max_fat: Double,
    val max_carbs: Double
)

data class AnalyzeFoodRequest(
    val user_id: Int,
    val sugar: Double,
    val sodium: Double,
    val fat: Double,
    val carbs: Double
)

data class AiPredictRequest(
    val sugar: Double,
    val sodium: Double,
    val fat: Double,
    val carbs: Double,
    val calories: Double,
    val protein: Double,
    val fiber: Double
)

data class SaveScanRequest(
    val user_id: Int,
    val barcode: String? = "",
    val product_name: String,
    val analysis: String,
    val prediction: String,
    val image_url: String? = ""
)

data class GetProductDataRequest(
    val barcode: String
)

data class ProductDataResponse(
    val product_name: String,
    val brand: String? = "",
    val quantity: String? = "",
    val image_url: String? = "",
    val sugar: Double,
    val sodium: Double,
    val fat: Double,
    val carbs: Double,
    val calories: Double,
    val protein: Double,
    val fiber: Double,
    val error: String? = null
)

data class ScanItem(
    val product_name: String,
    val barcode: String? = "",
    val analysis_result: String,
    val ai_prediction: String,
    val scanned_at: String,
    val image_url: String? = null
)

data class UserProfileResponse(
    val id: Int,
    val full_name: String? = null,
    val email: String? = null,
    val profile_image_url: String? = null,
    val age: Int,
    val gender: String,
    val height_cm: Double,
    val weight_kg: Double,
    val activity_level: String,
    val sleep_hours: Double,
    val stress_level: String,
    val conditions: List<ConditionItem> = emptyList()
)

data class ConditionItem(
    val disease_name: String,
    val stage: String
)

data class HistoryResponse(
    val history: List<ScanItem>
)

data class ApiResponse(
    val message: String? = null,
    val analysis: String? = null,
    val prediction: String? = null,
    val user_id: Int? = null,
    val full_name: String? = null,
    val error: String? = null
)

data class UpdateProfileRequest(
    val user_id: Int,
    val full_name: String,
    val email: String? = null,
    val password: String? = null,
    val profile_image_url: String? = null,
    val age: Int,
    val gender: String,
    val height_cm: Double,
    val weight_kg: Double,
    val activity_level: String,
    val sleep_hours: Double,
    val stress_level: String
)

data class ResetPasswordRequest(
    val email: String,
    val new_password: String
)

// --- API Interface ---
interface ApiService {
    @POST("/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<ApiResponse>

    @POST("/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<ApiResponse>

    @POST("/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse>

    @POST("/add-health-profile")
    suspend fun addHealthProfile(@Body request: HealthProfileRequest): Response<ApiResponse>

    @POST("/add-condition")
    suspend fun addCondition(@Body request: ConditionRequest): Response<ApiResponse>

    @POST("/save-health-condition")
    suspend fun saveHealthCondition(@Body request: SaveConditionRequest): Response<ApiResponse>

    @POST("/set-nutrition-limit")
    suspend fun setNutritionLimit(@Body request: NutritionLimitRequest): Response<ApiResponse>

    @POST("/analyze-food")
    suspend fun analyzeFood(@Body request: AnalyzeFoodRequest): Response<ApiResponse>

    @POST("/ai-predict")
    suspend fun aiPredict(@Body request: AiPredictRequest): Response<ApiResponse>

    @POST("/save-scan")
    suspend fun saveScan(@Body request: SaveScanRequest): Response<ApiResponse>

    @retrofit2.http.GET("/get-history")
    suspend fun getHistory(@retrofit2.http.Query("user_id") userId: Int): Response<HistoryResponse>

    @retrofit2.http.GET("/get-profile")
    suspend fun getProfile(@retrofit2.http.Query("user_id") userId: Int): Response<UserProfileResponse>

    @POST("/get-product-data")
    suspend fun getProductData(@Body request: GetProductDataRequest): Response<ProductDataResponse>

    @POST("/update-profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse>

    @retrofit2.http.Multipart
    @POST("/upload-profile-image")
    suspend fun uploadProfileImage(
        @retrofit2.http.Part("user_id") userId: okhttp3.RequestBody,
        @retrofit2.http.Part image: okhttp3.MultipartBody.Part
    ): Response<ApiResponse>

    @POST("/remove-condition")
    suspend fun removeCondition(@Body request: SaveConditionRequest): Response<ApiResponse>
}
