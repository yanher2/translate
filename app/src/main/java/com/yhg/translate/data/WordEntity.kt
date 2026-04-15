package com.yhg.translate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val englishWord: String,
    val chineseTranslation: String,
    val timestamp: Long = System.currentTimeMillis()
)
