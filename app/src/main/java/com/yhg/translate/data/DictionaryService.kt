package com.yhg.translate.data

import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 词典查询服务
 */
class DictionaryService(context: Context) {

    private val dbHelper = DictionaryDatabase(context)

    /**
     * 查询单词翻译
     * @param word 英文单词
     * @return 中文翻译，如果未找到返回 null
     */
    suspend fun translate(word: String): String? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            // 查询 stardict 表
            cursor = db.query(
                "stardict",  // 表名
                arrayOf("translation"),  // 查询字段
                "word = ?",  // WHERE 条件
                arrayOf(word.lowercase()),  // WHERE 参数
                null,  // GROUP BY
                null,  // HAVING
                null   // ORDER BY
            )

            if (cursor.moveToFirst()) {
                val translationIndex = cursor.getColumnIndex("translation")
                if (translationIndex != -1) {
                    return@withContext cursor.getString(translationIndex)
                }
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            cursor?.close()
        }
    }

    /**
     * 查询单词详细信息（包含音标、释义等）
     * @param word 英文单词
     * @return 单词详细信息，如果未找到返回 null
     */
    suspend fun getWordDetail(word: String): WordDetail? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(
                "stardict",
                arrayOf("word", "phonetic", "translation", "definition", "pos", "collins", "oxford"),
                "word = ?",
                arrayOf(word.lowercase()),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                val wordIndex = cursor.getColumnIndex("word")
                val phoneticIndex = cursor.getColumnIndex("phonetic")
                val translationIndex = cursor.getColumnIndex("translation")
                val definitionIndex = cursor.getColumnIndex("definition")
                val posIndex = cursor.getColumnIndex("pos")
                val collinsIndex = cursor.getColumnIndex("collins")
                val oxfordIndex = cursor.getColumnIndex("oxford")

                if (wordIndex != -1 && translationIndex != -1) {
                    return@withContext WordDetail(
                        word = cursor.getString(wordIndex),
                        phonetic = if (phoneticIndex != -1) cursor.getString(phoneticIndex) else null,
                        translation = cursor.getString(translationIndex),
                        definition = if (definitionIndex != -1) cursor.getString(definitionIndex) else null,
                        pos = if (posIndex != -1) cursor.getString(posIndex) else null,
                        collins = if (collinsIndex != -1) cursor.getInt(collinsIndex) else 0,
                        oxford = if (oxfordIndex != -1) cursor.getInt(oxfordIndex) else 0
                    )
                }
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            cursor?.close()
        }
    }

    /**
     * 检查词典是否包含单词
     */
    suspend fun hasWord(word: String): Boolean {
        return translate(word) != null
    }

    /**
     * 模糊查询单词（前缀匹配）
     * @param prefix 单词前缀
     * @param limit 返回结果数量限制
     * @return 匹配的单词列表
     */
    suspend fun searchByPrefix(prefix: String, limit: Int = 10): List<String> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(
                "stardict",
                arrayOf("word"),
                "word LIKE ?",
                arrayOf("$prefix%"),
                null,
                null,
                "word ASC",
                limit.toString()
            )

            val results = mutableListOf<String>()
            while (cursor.moveToNext()) {
                val wordIndex = cursor.getColumnIndex("word")
                if (wordIndex != -1) {
                    results.add(cursor.getString(wordIndex))
                }
            }
            return@withContext results
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        } finally {
            cursor?.close()
        }
    }

    /**
     * 模糊查询单词（包含匹配）
     * @param keyword 关键词
     * @param limit 返回结果数量限制
     * @return 匹配的单词列表
     */
    suspend fun searchByKeyword(keyword: String, limit: Int = 20): List<WordSearchResult> =
        withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            var cursor: Cursor? = null

            try {
                cursor = db.query(
                    "stardict",
                    arrayOf("word", "translation"),
                    "word LIKE ? OR translation LIKE ?",
                    arrayOf("%$keyword%", "%$keyword%"),
                    null,
                    null,
                    "CASE WHEN word LIKE '$keyword%' THEN 1 ELSE 2 END, word ASC",
                    limit.toString()
                )

                val results = mutableListOf<WordSearchResult>()
                while (cursor.moveToNext()) {
                    val wordIndex = cursor.getColumnIndex("word")
                    val translationIndex = cursor.getColumnIndex("translation")

                    if (wordIndex != -1 && translationIndex != -1) {
                        results.add(
                            WordSearchResult(
                                word = cursor.getString(wordIndex),
                                translation = cursor.getString(translationIndex)
                            )
                        )
                    }
                }
                return@withContext results
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            } finally {
                cursor?.close()
            }
        }
}

/**
 * 单词详细信息
 */
data class WordDetail(
    val word: String,
    val phonetic: String?,
    val translation: String,
    val definition: String?,
    val pos: String?,
    val collins: Int,
    val oxford: Int
)

/**
 * 单词搜索结果
 */
data class WordSearchResult(
    val word: String,
    val translation: String
)
