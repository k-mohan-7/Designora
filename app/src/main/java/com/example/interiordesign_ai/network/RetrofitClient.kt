package com.example.interiordesign_ai.network

import com.example.interiordesign_ai.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Interceptor that injects Cloudflare credentials as custom headers
     * on every request to the AI endpoint. The PHP proxy reads them
     * from headers — no credentials stored on the server at all.
     */
    private val credentialInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val enhanced = original.newBuilder()
            .header("X-Cf-Acct", ApiKeyProvider.accountId())
            .header("X-Cf-Tok",  ApiKeyProvider.token())
            .build()
        chain.proceed(enhanced)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(credentialInterceptor)
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)   // AI generation can take 30-60s
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
