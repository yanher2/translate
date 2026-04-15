package com.yhg.translate.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object TranslationService {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    suspend fun translateText(text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 使用免费的翻译API（这里使用MyMemory Translation API）
            val encodedText = java.net.URLEncoder.encode(text, "UTF-8")
            val url = "https://api.mymemory.translated.net/get?q=$encodedText&langpair=en|zh"
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Translation failed: ${response.code}"))
            }
            
            val responseBody = response.body?.string() ?: return@withContext Result.failure(
                Exception("Empty response")
            )
            
            // 解析MyMemory API响应
            val jsonResponse = org.json.JSONObject(responseBody)
            val translatedText = jsonResponse.getJSONObject("responseData")
                .getString("translatedText")
            
            Result.success(translatedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
