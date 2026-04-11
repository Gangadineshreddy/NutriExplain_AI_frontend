package com.SIMATS.nutriai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.nutriai.network.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class NutriViewModel : ViewModel() {
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    
    // In a real app, you'd store the user_id after login/signup
    var currentUserId by mutableStateOf(-1) 
    var userName by mutableStateOf("") 
    var userEmail by mutableStateOf("") 

    // Onboarding Data
    var onboardingAge by mutableStateOf("")
    var onboardingGender by mutableStateOf("MALE")
    var onboardingHeight by mutableStateOf("")
    var onboardingWeight by mutableStateOf("")
    var onboardingSleep by mutableStateOf(7.5f)
    var onboardingActivityDays by mutableStateOf(3f)
    var onboardingStress by mutableStateOf("Medium")
    var onboardingDiseases by mutableStateOf<List<DiseaseEntry>>(emptyList())
    
    // AI Prediction & Analysis Results
    var aiPredictionResult by mutableStateOf<String?>(null)
    var foodAnalysisResult by mutableStateOf<String?>(null)
    var scannedProduct by mutableStateOf<ProductDataResponse?>(null)
    var currentBarcode by mutableStateOf<String?>(null)
    
    var aiAnalysisLoading by mutableStateOf(false)
    var foodAnalysisLoading by mutableStateOf(false)

    val isAnalysisComplete: Boolean
        get() = !aiAnalysisLoading && !foodAnalysisLoading && aiPredictionResult != null && foodAnalysisResult != null
    
    // History & Profile
    var scanHistory by mutableStateOf<List<ScanItem>>(emptyList())
    var userProfile by mutableStateOf<UserProfileResponse?>(null)

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.instance.registerUser(
                    RegisterRequest(name, email, pass)
                )
                if (response.isSuccessful && response.body()?.user_id != null) {
                    val body = response.body()!!
                    currentUserId = body.user_id!!
                    userEmail = email
                    userName = name
                    fetchProfileData()
                    onSuccess()
                } else {
                    errorMessage = response.body()?.error ?: "Registration failed"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (email.isBlank() || pass.isBlank()) {
                errorMessage = "Email and Password cannot be empty"
                return@launch
            }
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.instance.loginUser(
                    LoginRequest(email, pass)
                )
                if (response.isSuccessful && response.body()?.user_id != null) {
                    val body = response.body()!!
                    currentUserId = body.user_id!!
                    userEmail = email
                    userName = body.full_name ?: ""
                    fetchProfileData()
                    onSuccess()
                } else {
                    errorMessage = response.body()?.error ?: "Login failed"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitOnboardingProfile(diseases: List<DiseaseEntry>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Add Health Profile
                val healthResponse = RetrofitClient.instance.addHealthProfile(
                    HealthProfileRequest(
                        user_id = currentUserId,
                        age = onboardingAge.toIntOrNull() ?: 25,
                        gender = onboardingGender,
                        height_cm = onboardingHeight.toDoubleOrNull() ?: 170.0,
                        weight_kg = onboardingWeight.toDoubleOrNull() ?: 70.0,
                        activity_level = if (onboardingActivityDays > 4) "HIGH" else if (onboardingActivityDays > 2) "MODERATE" else "LOW",
                        sleep_hours = onboardingSleep.toDouble(),
                        stress_level = onboardingStress
                    )
                )
                
                if (!healthResponse.isSuccessful) {
                    errorMessage = "Failed to save profile"
                    isLoading = false
                    return@launch
                }

                // 2. Add Medical Conditions & Stages
                val diseaseMap = mapOf(
                    "Diabetes" to 1,
                    "Hypertension" to 2,
                    "Heart Disease" to 3,
                    "Obesity" to 4,
                    "Kidney Disease" to 5
                )

                for (disease in diseases) {
                    // ID based (user_medical_conditions)
                    val conditionId = diseaseMap[disease.name] ?: 99
                    RetrofitClient.instance.addCondition(
                        ConditionRequest(currentUserId, conditionId)
                    )
                    
                    // Name & Stage based (user_conditions)
                    RetrofitClient.instance.saveHealthCondition(
                        SaveConditionRequest(currentUserId, disease.name, disease.stage)
                    )
                }

                // 3. Set Nutrition Limits (mocked simple calculation based on weight)
                val weight = onboardingWeight.toDoubleOrNull() ?: 70.0
                val limitResponse = RetrofitClient.instance.setNutritionLimit(
                    NutritionLimitRequest(
                        user_id = currentUserId,
                        max_sugar = 30.0, // Fixed for simplicity, can be dynamic
                        max_sodium = 2300.0,
                        max_fat = weight * 1.0, 
                        max_carbs = weight * 4.0
                    )
                )

                if (limitResponse.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = "Failed to set nutrition limits"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun analyzeFood(sugar: Double, sodium: Double, fat: Double, carbs: Double, onResult: (String) -> Unit) {
        viewModelScope.launch {
            foodAnalysisLoading = true
            try {
                val response = RetrofitClient.instance.analyzeFood(
                    AnalyzeFoodRequest(currentUserId, sugar, sodium, fat, carbs)
                )
                if (response.isSuccessful) {
                    val result = response.body()?.analysis ?: "UNKNOWN"
                    foodAnalysisResult = result
                    onResult(result)
                }
            } catch (e: Exception) {
                errorMessage = "Analysis failed: ${e.message}"
            } finally {
                foodAnalysisLoading = false
            }
        }
    }

    fun aiPredict(sugar: Double, sodium: Double, fat: Double, carbs: Double, calories: Double, protein: Double, fiber: Double) {
        viewModelScope.launch {
            aiAnalysisLoading = true
            try {
                val response = RetrofitClient.instance.aiPredict(
                    AiPredictRequest(sugar, sodium, fat, carbs, calories, protein, fiber)
                )
                if (response.isSuccessful) {
                    aiPredictionResult = response.body()?.prediction
                }
            } catch (e: Exception) {
                errorMessage = "AI Prediction failed: ${e.message}"
            } finally {
                aiAnalysisLoading = false
            }
        }
    }

    fun saveScanResult(productName: String, barcode: String?, imageUrl: String? = "") {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.saveScan(
                    SaveScanRequest(
                        user_id = currentUserId,
                        barcode = barcode,
                        product_name = productName,
                        analysis = foodAnalysisResult ?: "SAFE",
                        prediction = aiPredictionResult ?: "SAFE",
                        image_url = imageUrl
                    )
                )
            } catch (e: Exception) {
                // Background task, maybe just log it
            }
        }
    }

    fun loadScanDetails(item: ScanItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Restore the results from history
                aiPredictionResult = item.ai_prediction
                foodAnalysisResult = item.analysis_result
                
                // Fetch the full product data using the barcode
                val barcode = item.barcode
                if (!barcode.isNullOrBlank()) {
                    val response = RetrofitClient.instance.getProductData(GetProductDataRequest(barcode))
                    if (response.isSuccessful) {
                        scannedProduct = response.body()
                        onComplete()
                    } else {
                        errorMessage = "Failed to load product details"
                    }
                } else {
                    // Fallback: create a dummy product with what we have
                    scannedProduct = ProductDataResponse(
                        product_name = item.product_name,
                        image_url = item.image_url,
                        sugar = 0.0, sodium = 0.0, fat = 0.0, carbs = 0.0, calories = 0.0, protein = 0.0, fiber = 0.0
                    )
                    onComplete()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchHistory() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getHistory(currentUserId)
                if (response.isSuccessful) {
                    scanHistory = response.body()?.history ?: emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load history: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            fetchProfileData()
        }
    }

    private suspend fun fetchProfileData() {
        isLoading = true
        try {
            println("Fetching profile for user: $currentUserId")
            val response = RetrofitClient.instance.getProfile(currentUserId)
            if (response.isSuccessful) {
                userProfile = response.body()
                println("Profile fetched successfully: ${userProfile?.full_name}")
            } else {
                println("Failed to fetch profile: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load profile: ${e.message}"
            println("Error fetching profile: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    fun fetchProductData(barcode: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.instance.getProductData(GetProductDataRequest(barcode))
                if (response.isSuccessful && response.body() != null) {
                    scannedProduct = response.body()
                    currentBarcode = barcode
                    onResult(true)
                } else {
                    errorMessage = response.body()?.error ?: "Product not found"
                    onResult(false)
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProfile(
        name: String,
        email: String?,
        password: String?,
        profileImageUrl: String?,
        age: Int,
        gender: String,
        height: Double,
        weight: Double,
        activity: String,
        sleep: Double,
        stress: String,
        onResult: (Boolean) -> Unit
    ) {
        if (currentUserId == -1) {
            println("Cannot update profile: No user logged in")
            onResult(false)
            return
        }
        viewModelScope.launch {
            try {
                val request = UpdateProfileRequest(
                    user_id = currentUserId,
                    full_name = name,
                    email = if (email.isNullOrBlank()) null else email,
                    password = if (password.isNullOrBlank()) null else password,
                    profile_image_url = profileImageUrl,
                    age = age,
                    gender = gender,
                    height_cm = height,
                    weight_kg = weight,
                    activity_level = activity,
                    sleep_hours = sleep,
                    stress_level = stress
                )
                println("Updating profile with request: $request")
                val response = RetrofitClient.instance.updateProfile(request)
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.error != null) {
                        println("Backend reported error: ${apiResponse.error}")
                        errorMessage = apiResponse.error
                        onResult(false)
                    } else {
                        println("Profile updated successfully on backend: ${apiResponse?.message}")
                        // Update local primary states immediately for faster UI feedback
                        userName = name
                        if (!email.isNullOrBlank()) userEmail = email
                        
                        fetchProfileData() // Refresh full local profile synchronously for other data
                        onResult(true)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("API call failed: ${response.code()} - $errorBody")
                    errorMessage = "Update failed: ${response.code()}"
                    onResult(false)
                }
            } catch (e: Exception) {
                println("Exception during update: ${e.message}")
                onResult(false)
            }
        }
    }

    fun uploadProfileImage(context: android.content.Context, uri: android.net.Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                
                val requestFile = okhttp3.RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    bytes
                )
                
                val body = MultipartBody.Part.createFormData("image", "profile_${currentUserId}.jpg", requestFile)
                val userIdPart = currentUserId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = RetrofitClient.instance.uploadProfileImage(userIdPart, body)
                if (response.isSuccessful) {
                    fetchProfileData()
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun addCondition(disease: String, stage: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                println("Adding condition: $disease, $stage for user: $currentUserId")
                val response = RetrofitClient.instance.saveHealthCondition(
                    com.SIMATS.nutriai.network.SaveConditionRequest(currentUserId, disease, stage)
                )
                if (response.isSuccessful) {
                    println("Condition added successfully")
                    fetchProfileData()
                    onResult(true)
                } else {
                    println("Failed to add condition: ${response.code()} - ${response.errorBody()?.string()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                println("Exception adding condition: ${e.message}")
                onResult(false)
            }
        }
    }

    fun removeCondition(disease: String, stage: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                println("Removing condition: $disease, $stage for user: $currentUserId")
                val response = RetrofitClient.instance.removeCondition(
                    com.SIMATS.nutriai.network.SaveConditionRequest(currentUserId, disease, stage)
                )
                if (response.isSuccessful) {
                    println("Condition removed successfully")
                    fetchProfileData()
                    onResult(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Failed to remove condition: ${response.code()} - $errorBody")
                    errorMessage = "Delete failed: $errorBody"
                    onResult(false)
                }
            } catch (e: Exception) {
                println("Exception removing condition: ${e.message}")
                onResult(false)
            }
        }
    }

    fun resetPassword(email: String, newPass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.instance.resetPassword(
                    com.SIMATS.nutriai.network.ResetPasswordRequest(email, newPass)
                )
                if (response.isSuccessful) {
                    onResult(true)
                } else {
                    errorMessage = response.body()?.error ?: "Reset failed"
                    onResult(false)
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }
}
