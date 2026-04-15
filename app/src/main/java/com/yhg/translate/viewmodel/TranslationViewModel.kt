package com.yhg.translate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhg.translate.network.TranslationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslationViewModel : ViewModel() {
    
    private val _englishText = MutableStateFlow("")
    val englishText: StateFlow<String> = _englishText.asStateFlow()
    
    private val _chineseTranslation = MutableStateFlow("")
    val chineseTranslation: StateFlow<String> = _chineseTranslation.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun setEnglishText(text: String) {
        _englishText.value = text
    }
    
    fun translate() {
        val text = _englishText.value.trim()
        if (text.isEmpty()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = TranslationService.translateText(text)
            result.onSuccess { translatedText ->
                _chineseTranslation.value = translatedText
            }.onFailure { exception ->
                _error.value = exception.message ?: "翻译失败"
                _chineseTranslation.value = ""
            }
            
            _isLoading.value = false
        }
    }
    
    fun clear() {
        _englishText.value = ""
        _chineseTranslation.value = ""
        _error.value = null
    }
}
