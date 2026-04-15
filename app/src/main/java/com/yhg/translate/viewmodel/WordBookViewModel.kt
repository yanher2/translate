package com.yhg.translate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yhg.translate.data.DatabaseProvider
import com.yhg.translate.data.WordEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordBookViewModel(application: Application) : AndroidViewModel(application) {

    private val _words = MutableStateFlow<List<WordEntity>>(emptyList())
    val words: StateFlow<List<WordEntity>> = _words.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val dao by lazy { DatabaseProvider.getDatabase(getApplication()).wordDao() }

    init {
        // 自动加载单词，Flow会持续更新
        viewModelScope.launch {
            _isLoading.value = true
            dao.getAllWords().collect { wordList ->
                _words.value = wordList
                _isLoading.value = false
            }
        }
    }

    fun addWord(englishWord: String, chineseTranslation: String) {
        viewModelScope.launch {
            try {
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

    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            try {
                dao.deleteWord(word)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAllWords() {
        viewModelScope.launch {
            try {
                dao.deleteAllWords()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
