package com.yhg.translate.network

import android.content.Context
import com.yhg.translate.data.DictionaryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TranslationService {

    private var dictionaryService: DictionaryService? = null

    /**
     * 初始化词典服务
     */
    fun initialize(context: Context) {
        if (dictionaryService == null) {
            dictionaryService = DictionaryService(context)
        }
    }

    /**
     * 翻译文本 - 使用 SQLite 离线词典
     * @param text 要翻译的英文文本
     * @return 中文翻译，如果找不到返回提示信息
     */
    suspend fun translateText(text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val trimmedText = text.trim()

            if (trimmedText.isEmpty()) {
                return@withContext Result.success("")
            }

            // 确保词典已初始化
            val dict = dictionaryService
                ?: throw IllegalStateException("DictionaryService not initialized. Call TranslationService.initialize(context) first.")

            // 查询 SQLite 词典
            val translation = dict.translate(trimmedText)

            if (translation != null) {
                // 找到翻译
                Result.success(translation)
            } else {
                // 未找到翻译，返回提示信息
                val message = "词典暂无此词：$trimmedText\n\n提示：\n• 本应用使用离线词典（30万+ 词汇）\n• 如果词典数据库正在初始化，请稍后再试\n• 您可以尝试简化单词或使用更常见的表达"
                Result.success(message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取单词详细信息
     * @param word 英文单词
     * @return 单词详细信息
     */
    suspend fun getWordDetail(word: String): Result<com.yhg.translate.data.WordDetail> = withContext(Dispatchers.IO) {
        try {
            val dict = dictionaryService
                ?: throw IllegalStateException("DictionaryService not initialized")

            val detail = dict.getWordDetail(word)
            if (detail != null) {
                Result.success(detail)
            } else {
                Result.failure(Exception("单词未找到：$word"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 搜索单词（前缀匹配）
     * @param prefix 单词前缀
     * @param limit 返回结果数量限制
     * @return 匹配的单词列表
     */
    suspend fun searchByPrefix(prefix: String, limit: Int = 10): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val dict = dictionaryService
                ?: throw IllegalStateException("DictionaryService not initialized")

            val results = dict.searchByPrefix(prefix, limit)
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
