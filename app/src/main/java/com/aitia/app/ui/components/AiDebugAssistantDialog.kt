package com.aitia.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aitia.app.domain.insights.AiChatMessage
import com.aitia.app.domain.insights.LocalAiDebugAssistant
import com.aitia.app.domain.model.Issue
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.ui.theme.MonospaceCode
import com.aitia.app.ui.theme.StatusFixed
import com.aitia.app.util.rememberHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiDebugAssistantDialog(
    issue: Issue,
    onDismiss: () -> Unit,
    onApplySolution: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember {
        mutableStateListOf<AiChatMessage>().apply {
            add(LocalAiDebugAssistant.analyzeIssue(issue))
        }
    }

    var userInput by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    fun sendMessage(query: String) {
        if (query.isBlank()) return
        haptic.lightTap()
        messages.add(AiChatMessage(isUser = true, text = query))
        userInput = ""
        isThinking = true

        scope.launch {
            delay(400) // Realistic local inference micro-delay
            val aiResponse = LocalAiDebugAssistant.answerUserQuery(query, issue)
            messages.add(aiResponse)
            isThinking = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFF00F0FF).copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            color = Color(0xFF0D1117)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00F0FF).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Aitia AI Debug Assistant",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "100% OFFLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00FF88),
                                    modifier = Modifier
                                        .background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Defect #${issue.id} · ${issue.title}",
                                fontSize = 12.sp,
                                color = Color(0xFF8B949E),
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B949E))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Prompt Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val promptOptions = listOf(
                        "🧠 Root Cause" to "Why did this crash happen?",
                        "🛠️ Code Fix" to "Generate code fix patch",
                        "🧪 Unit Test" to "Generate automated unit test",
                        "💡 In Plain English" to "Explain in plain English"
                    )

                    promptOptions.forEach { (chipLabel, queryText) ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(16.dp))
                                .clickable { sendMessage(queryText) },
                            color = Color(0xFF161B22)
                        ) {
                            Text(
                                text = chipLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00F0FF),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chat Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        if (msg.isUser) {
                            // User Message
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    color = AitiaBlue,
                                    shape = RoundedCornerShape(14.dp, 14.dp, 2.dp, 14.dp),
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        } else {
                            // AI Assistant Message
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp, 14.dp, 14.dp, 2.dp))
                                    .background(Color(0xFF161B22))
                                    .border(1.dp, Color(0xFF30363D), RoundedCornerShape(14.dp, 14.dp, 14.dp, 2.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = Color(0xFFC9D1D9),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                if (msg.codeSnippet != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = Color(0xFF0D1117),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "GENERATED CODE",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00F0FF)
                                                )
                                                Row {
                                                    TextButton(onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(ClipData.newPlainText("AI Code", msg.codeSnippet))
                                                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                    }) {
                                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Copy", color = Color.White, fontSize = 11.sp)
                                                    }

                                                    if (msg.suggestedAction?.contains("Fix") == true || msg.suggestedAction?.contains("Solution") == true) {
                                                        TextButton(onClick = {
                                                            onApplySolution(msg.codeSnippet)
                                                            Toast.makeText(context, "Fix applied to issue!", Toast.LENGTH_SHORT).show()
                                                        }) {
                                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Apply", color = Color(0xFF00FF88), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = msg.codeSnippet,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                color = Color(0xFF7EE787),
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isThinking) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF00F0FF)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aitia AI is analyzing defect patterns...", color = Color(0xFF8B949E), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        placeholder = { Text("Ask follow-up question or request test...", color = Color(0xFF8B949E), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00F0FF),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage(userInput) })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { sendMessage(userInput) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF00F0FF))
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                    }
                }
            }
        }
    }
}
