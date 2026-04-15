package com.yhg.translate.data

import android.content.Context

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = androidx.room.Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "translate_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
