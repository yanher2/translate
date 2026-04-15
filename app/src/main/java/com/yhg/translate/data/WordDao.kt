package com.yhg.translate.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY timestamp DESC")
    fun getAllWords(): Flow<List<WordEntity>>
    
    @Query("SELECT * FROM words WHERE englishWord = :englishWord LIMIT 1")
    suspend fun getWordByEnglish(englishWord: String): WordEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)
    
    @Delete
    suspend fun deleteWord(word: WordEntity)
    
    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Long)
    
    @Query("DELETE FROM words")
    suspend fun deleteAllWords()
}
