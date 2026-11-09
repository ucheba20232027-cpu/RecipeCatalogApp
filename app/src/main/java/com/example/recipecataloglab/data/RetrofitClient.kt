package com.example.recipecataloglab.data

import com.example.recipecataloglab.data.security.RequestSigner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    // Интерцептор для добавления HMAC-подписи
    private val signatureInterceptor = Interceptor { chain ->
        val original = chain.request()

        // Получаем заголовки с подписью
        val headers = RequestSigner.generateHeaders()

        val requestBuilder = original.newBuilder()
        headers.forEach { (key, value) ->
            requestBuilder.header(key, value)
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(signatureInterceptor) // <-- Добавляем подпись
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: PostsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostsApi::class.java)
    }
}