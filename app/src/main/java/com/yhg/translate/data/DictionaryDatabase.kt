package com.yhg.translate.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

/**
 * 词典数据库管理器
 * 从 assets 复制词典数据库到应用数据库目录
 */
class DictionaryDatabase(private val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "stardict.db"
        private const val DATABASE_VERSION = 1
        private const val ASSETS_PATH = "dic/stardict.db"
    }

    /**
     * 初始化数据库：从 assets 复制到数据库目录
     */
    fun initializeDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        // 如果数据库已存在，不再复制
        if (dbFile.exists()) {
            return
        }

        // 确保父目录存在
        dbFile.parentFile?.mkdirs()

        // 从 assets 复制数据库文件
        try {
            context.assets.open(ASSETS_PATH).use { inputStream ->
                FileOutputStream(dbFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to copy dictionary database from assets", e)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 数据库文件已从 assets 复制，不需要创建表
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 版本升级时重新复制数据库
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (dbFile.exists()) {
            dbFile.delete()
        }
        initializeDatabase()
    }

    /**
     * 获取可读数据库
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        initializeDatabase()
        return super.getReadableDatabase()
    }

    /**
     * 获取可写数据库
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        initializeDatabase()
        return super.getWritableDatabase()
    }
}
