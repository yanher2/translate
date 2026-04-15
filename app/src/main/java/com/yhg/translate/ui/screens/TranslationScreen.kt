package com.yhg.translate.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy as ContentCopyIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yhg.translate.viewmodel.TranslationViewModel
import com.yhg.translate.viewmodel.WordBookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationBottomSheet(
    viewModel: TranslationViewModel,
    wordBookViewModel: WordBookViewModel,
    initialText: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val englishText by viewModel.englishText.collectAsState()
    val chineseTranslation by viewModel.chineseTranslation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showAddToWordbookDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(initialText) {
        if (initialText.isNotEmpty()) {
            viewModel.setEnglishText(initialText)
            viewModel.translate()
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "翻译",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                
                // English Text Input
                OutlinedTextField(
                    value = englishText,
                    onValueChange = { viewModel.setEnglishText(it) },
                    label = { Text("英文文本") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    enabled = !isLoading
                )
                
                // Translate Button
                Button(
                    onClick = { viewModel.translate() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = englishText.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("翻译")
                    }
                }
                
                // Chinese Translation Result
                if (chineseTranslation.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "中文翻译",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chineseTranslation,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 18.sp
                            )
                        }
                    }
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Copy Button
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(chineseTranslation))
                                Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopyIcon, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("复制")
                        }
                        
                        // Add to Wordbook Button
                        Button(
                            onClick = { showAddToWordbookDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("加入单词本")
                        }
                    }
                }
                
                // Error Message
                if (error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error!!,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
    
    // Add to Wordbook Confirmation Dialog
    if (showAddToWordbookDialog) {
        AlertDialog(
            onDismissRequest = { showAddToWordbookDialog = false },
            title = { Text("加入单词本") },
            text = {
                Column {
                    Text("英文单词:")
                    Text(
                        text = englishText,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text("中文翻译:")
                    Text(
                        text = chineseTranslation,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        wordBookViewModel.addWord(context, englishText, chineseTranslation)
                        showAddToWordbookDialog = false
                        Toast.makeText(context, "已加入单词本", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddToWordbookDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
