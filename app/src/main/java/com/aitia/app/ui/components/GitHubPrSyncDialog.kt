package com.aitia.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import com.aitia.app.domain.model.Issue
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.util.GitRemoteSyncManager
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun GitHubPrSyncDialog(
    issue: Issue,
    githubPat: String,
    defaultRepo: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    val scope = rememberCoroutineScope()

    var repoOwner by remember { mutableStateOf(defaultRepo.substringBefore("/", "CodeSorcerer-007")) }
    var repoName by remember { mutableStateOf(defaultRepo.substringAfter("/", "Aitia")) }
    var baseBranch by remember { mutableStateOf("main") }
    var platformType by remember { mutableStateOf(0) } // 0: GitHub, 1: GitLab

    val payload = remember(issue, repoOwner, repoName, baseBranch) {
        GitRemoteSyncManager.generatePullRequestPayload(
            issue = issue,
            repoOwner = repoOwner,
            repoName = repoName,
            baseBranch = baseBranch
        )
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
                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(20.dp)),
            color = Color(0xFF0D1117)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🐙 Remote PR & Issue Sync",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Generate GitHub & GitLab Pull Requests with 1 tap",
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B949E))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Platform Selector
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = platformType == 0,
                            onClick = { platformType = 0 },
                            label = { Text("GitHub", fontWeight = FontWeight.Bold) }
                        )
                        FilterChip(
                            selected = platformType == 1,
                            onClick = { platformType = 1 },
                            label = { Text("GitLab", fontWeight = FontWeight.Bold) }
                        )
                    }

                    // Repo Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = repoOwner,
                            onValueChange = { repoOwner = it },
                            label = { Text("Owner / Org", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = repoName,
                            onValueChange = { repoName = it },
                            label = { Text("Repository", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    // Generated Branch & PR Title Card
                    Surface(
                        color = Color(0xFF161B22),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("PR TITLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                            Text(payload.prTitle, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("BRANCH NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00FF88))
                            Text(payload.branchName, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF7EE787))
                        }
                    }

                    // GitHub CLI Command Card
                    Surface(
                        color = Color(0xFF161B22),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("GITHUB CLI COMMAND (`gh`)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF58A6FF))
                                TextButton(onClick = {
                                    haptic.success()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("gh CLI", payload.githubCliCommand))
                                    Toast.makeText(context, "gh command copied!", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", color = Color.White, fontSize = 11.sp)
                                }
                            }
                            Text(
                                text = payload.githubCliCommand,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFC9D1D9),
                                maxLines = 4
                            )
                        }
                    }

                    // Git CLI Push Sequence Card
                    Surface(
                        color = Color(0xFF161B22),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("GIT CLI COMMANDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB703))
                                TextButton(onClick = {
                                    haptic.success()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Git CLI", payload.gitCliSequence))
                                    Toast.makeText(context, "Git commands copied!", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", color = Color.White, fontSize = 11.sp)
                                }
                            }
                            Text(
                                text = payload.gitCliSequence,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFC9D1D9)
                            )
                        }
                    }

                    // PR Markdown Body Card
                    Surface(
                        color = Color(0xFF161B22),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("PULL REQUEST MARKDOWN BODY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBC8CFF))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = payload.prBodyMarkdown,
                                fontSize = 11.sp,
                                color = Color(0xFFC9D1D9),
                                maxLines = 8
                            )
                        }
                    }

                    if (githubPat.isNotBlank()) {
                        Button(
                            onClick = {
                                haptic.success()
                                scope.launch {
                                    val result = GitRemoteSyncManager.createGitHubIssue(
                                        issue = issue,
                                        githubPat = githubPat,
                                        defaultRepo = "$repoOwner/$repoName"
                                    )
                                    if (result.isSuccess) {
                                        Toast.makeText(context, "Issue Created Successfully!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.getOrNull()))
                                        context.startActivity(intent)
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "API Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1-Click Publish Issue via API", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val url = if (platformType == 0) payload.githubWebUrl else payload.gitlabWebUrl
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }.onFailure {
                                Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Web PR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("PR Body", payload.prBodyMarkdown))
                            Toast.makeText(context, "PR Markdown Body copied!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Copy PR Body", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
