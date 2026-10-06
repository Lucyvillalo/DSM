package com.example.retrofitcrudapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // En un proyecto propio de MockAPI, reemplazar Ãºnicamente esta URL.
    private const val BASE_URL = "https://66240d4504457d4aaf9b8530.mockapi.io/api/"
    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        Retrofit.Builder().baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .addConverterFactory(GsonConverterFactory.create(ApiJson.gson)).build()
    }
    val instance: AlumnoApi by lazy { retrofit.create(AlumnoApi::class.java) }
    val profesorInstance: ProfesorApi by lazy { retrofit.create(ProfesorApi::class.java) }
}
