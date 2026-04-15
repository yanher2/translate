package com.yhg.translate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhg.translate.data.DatabaseProvider
import com.yhg.translate.data.WordEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordBookViewModel : ViewModel() {
    
    private val _words = MutableStateFlow<List<WordEntity>>(emptyList())
    val words: StateFlow<List<WordEntity>> = _words.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadWords(context: android.content.Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dao = DatabaseProvider.getDatabase(context).wordDao()
                dao.getAllWords().collect { wordList ->
                    _words.value = wordList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addWord(context: android.content.Context, englishWord: String, chineseTranslation: String) {
        viewModelScope.launch {
            try {
                val dao = DatabaseProvider.getDatabase(context).wordDao()
                val existingWord = dao.getWordByEnglish(englishWord)
                if (existingWord == null) {
                    dao.insertWord(
                        WordEntity(
                            englishWord = englishWord,
                            chineseTranslation = chineseTranslation
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteWord(context: android.content.Context, word: WordEntity) {
        viewModelScope.launch {
            try {
                val dao = DatabaseProvider.getDatabase(context).wordDao()
                dao.deleteWord(word)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteAllWords(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val dao = DatabaseProvider.getDatabase(context).wordDao()
                dao.deleteAllWords()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
