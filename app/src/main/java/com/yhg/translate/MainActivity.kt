package com.yhg.translate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.yhg.translate.ui.screens.HomeScreen
import com.yhg.translate.ui.screens.TranslationBottomSheet
import com.yhg.translate.ui.screens.WordBookScreen
import com.yhg.translate.ui.theme.TranslateTheme
import com.yhg.translate.viewmodel.TranslationViewModel
import com.yhg.translate.viewmodel.WordBookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 处理从其他应用传递过来的文本
        val selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
        
        setContent {
            TranslateTheme {
                MainApp(initialText = selectedText ?: "")
            }
        }
    }
}

@Composable
fun MainApp(initialText: String) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showTranslationSheet by remember { mutableStateOf(false) }
    var sheetInitialText by remember { mutableStateOf(initialText) }
    
    val translationViewModel = remember { TranslationViewModel() }
    val wordBookViewModel = remember { WordBookViewModel() }
    
    // 监听初始文本
    LaunchedEffect(initialText) {
        if (initialText.isNotBlank()) {
            sheetInitialText = initialText
            showTranslationSheet = true
        }
    }
    
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                Screen.Home -> {
                    HomeScreen(
                        onOpenTranslation = {
                            sheetInitialText = ""
                            showTranslationSheet = true
                        },
                        onOpenWordBook = {
                            currentScreen = Screen.WordBook
                        }
                    )
                }
                Screen.WordBook -> {
                    WordBookScreen(
                        viewModel = wordBookViewModel,
                        onBack = { currentScreen = Screen.Home }
                    )
                }
            }
        }
    }
    
    // Translation Bottom Sheet
    if (showTranslationSheet) {
        TranslationBottomSheet(
            viewModel = translationViewModel,
            wordBookViewModel = wordBookViewModel,
            initialText = sheetInitialText,
            onDismiss = {
                showTranslationSheet = false
                translationViewModel.clear()
            }
        )
    }
}

sealed class Screen {
    object Home : Screen()
    object WordBook : Screen()
}
