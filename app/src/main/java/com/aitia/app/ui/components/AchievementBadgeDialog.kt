package com.aitia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aitia.app.domain.gamification.AchievementBadge
import com.aitia.app.domain.gamification.AchievementEngine
import com.aitia.app.domain.model.Issue
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.StatusFixed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementBadgeDialog(
    allIssues: List<Issue>,
    onDismiss: () -> Unit
) {
    val profile = remember(allIssues) {
        AchievementEngine.computeProfile(allIssues)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFFFFB703).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = Color(0xFF0D1117)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB703).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFB703),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🏆 Bug Slayer Trophies",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Level ${profile.level} · ${profile.rankTitle}",
                                fontSize = 12.sp,
                                color = Color(0xFFFFB703),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B949E))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // XP Level Banner
                Surface(
                    color = Color(0xFF161B22),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF30363D), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EXPERIENCE POINTS (XP)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B949E)
                            )
                            Text(
                                text = "${profile.totalXp} / ${profile.nextLevelXp} XP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val progressFraction = (profile.totalXp % 250) / 250f
                        LinearProgressIndicator(
                            progress = { progressFraction.coerceIn(0.05f, 1.0f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF00FF88),
                            trackColor = Color(0xFF30363D)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${profile.totalBugsSquashed}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Squashed", fontSize = 11.sp, color = Color(0xFF8B949E))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${profile.resolutionRatePercentage}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                                Text(text = "Resolution", fontSize = 11.sp, color = Color(0xFF8B949E))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val unlockedCount = profile.badges.count { it.isUnlocked }
                                Text(text = "$unlockedCount / ${profile.badges.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB703))
                                Text(text = "Badges", fontSize = 11.sp, color = Color(0xFF8B949E))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Badges Grid
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    profile.badges.forEach { badge ->
                        BadgeCard(badge = badge)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keep Slaying Bugs 🔥", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: AchievementBadge) {
    Surface(
        color = if (badge.isUnlocked) Color(0xFF161B22) else Color(0xFF161B22).copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (badge.isUnlocked) Color(0xFFFFB703).copy(alpha = 0.5f) else Color(0xFF30363D),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = badge.emoji,
                fontSize = 28.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) Color(0xFFFFB703).copy(alpha = 0.15f) else Color(0xFF30363D).copy(alpha = 0.3f))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (badge.isUnlocked) Color.White else Color(0xFF8B949E)
                    )
                    if (badge.isUnlocked) {
                        Text(
                            text = "UNLOCKED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00FF88),
                            modifier = Modifier
                                .background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    } else {
                        Text(
                            text = "${badge.progress}/${badge.maxProgress}",
                            fontSize = 10.sp,
                            color = Color(0xFF8B949E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = badge.description,
                    fontSize = 12.sp,
                    color = Color(0xFF8B949E),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
