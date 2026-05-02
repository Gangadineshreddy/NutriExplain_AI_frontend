package com.SIMATS.nutriai.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // --- ENVIRONMENT CONFIGURATION ---
    // Change the `currentEnvironment` variable below to switch between networks.
    
    // 1. USB Debugging (Requires running: adb -d reverse tcp:5000 tcp:5000)
    private const val ENV_USB = "http://127.0.0.1:5000/"
    
    // 2. Local WiFi / Real Device (Requires Windows Firewall port 5000 open)
    private const val ENV_WIFI = "http://10.187.90.54:5000/"
    
    // 3. Android Emulator
    private const val ENV_EMULATOR = "http://10.0.2.2:5000/"
    
    // 4. Production / Ngrok (External URL)
    private const val ENV_PROD = "https://your-production-url.com/"

    // 👉 CHANGE THIS LINE TO SWITCH ENVIRONMENTS:
    private const val BASE_URL = ENV_WIFI

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }
}
