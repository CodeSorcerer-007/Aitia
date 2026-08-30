package com.aitia.app.domain.gamification

import com.aitia.app.domain.model.Issue

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val maxProgress: Int
)

data class UserSlayerProfile(
    val level: Int,
    val rankTitle: String,
    val totalXp: Int,
    val nextLevelXp: Int,
    val totalBugsSquashed: Int,
    val resolutionRatePercentage: Int,
    val badges: List<AchievementBadge>
)

object AchievementEngine {

    fun computeProfile(allIssues: List<Issue>): UserSlayerProfile {
        val totalIssues = allIssues.size
        val resolvedIssues = allIssues.count { it.isResolved }
        val resolutionRate = if (totalIssues > 0) ((resolvedIssues.toDouble() / totalIssues) * 100).toInt() else 0

        val hasOcrOrVitals = allIssues.any { it.technicalDetails.isNotBlank() }
        val hasVoiceOrSteps = allIssues.any { it.stepsToReproduce.isNotBlank() }
        val hasSolutions = allIssues.count { it.solution.isNotBlank() }

        val badges = listOf(
            AchievementBadge(
                id = "first_blood",
                title = "First Blood",
                description = "Squashed your very first defect in Aitia",
                emoji = "🩸",
                isUnlocked = resolvedIssues >= 1,
                progress = resolvedIssues.coerceAtMost(1),
                maxProgress = 1
            ),
            AchievementBadge(
                id = "speed_demon",
                title = "Speed Demon",
                description = "Captured stack trace or hardware vitals",
                emoji = "⚡",
                isUnlocked = hasOcrOrVitals,
                progress = if (hasOcrOrVitals) 1 else 0,
                maxProgress = 1
            ),
            AchievementBadge(
                id = "radio_host",
                title = "Radio Host",
                description = "Recorded reproduction steps hands-free",
                emoji = "🎙️",
                isUnlocked = hasVoiceOrSteps,
                progress = if (hasVoiceOrSteps) 1 else 0,
                maxProgress = 1
            ),
            AchievementBadge(
                id = "code_healer",
                title = "Code Healer",
                description = "Documented 3 verified root-cause solutions",
                emoji = "🛡️",
                isUnlocked = hasSolutions >= 3,
                progress = hasSolutions.coerceAtMost(3),
                maxProgress = 3
            ),
            AchievementBadge(
                id = "master_slayer",
                title = "Master Bug Slayer",
                description = "Successfully squashed 5 defects",
                emoji = "🧙",
                isUnlocked = resolvedIssues >= 5,
                progress = resolvedIssues.coerceAtMost(5),
                maxProgress = 5
            ),
            AchievementBadge(
                id = "grandmaster",
                title = "Grandmaster Sorcerer",
                description = "Squashed 10+ defects to perfection",
                emoji = "👑",
                isUnlocked = resolvedIssues >= 10,
                progress = resolvedIssues.coerceAtMost(10),
                maxProgress = 10
            )
        )

        val unlockedCount = badges.count { it.isUnlocked }
        val totalXp = (resolvedIssues * 150) + (unlockedCount * 100) + (totalIssues * 25)
        val level = (totalXp / 250) + 1
        val nextLevelXp = level * 250

        val rankTitle = when (level) {
            1 -> "Apprentice Debugger"
            2 -> "Bug Hunter"
            3 -> "Senior Troubleshooter"
            4 -> "Master Debugger"
            else -> "Grandmaster Sorcerer"
        }

        return UserSlayerProfile(
            level = level,
            rankTitle = rankTitle,
            totalXp = totalXp,
            nextLevelXp = nextLevelXp,
            totalBugsSquashed = resolvedIssues,
            resolutionRatePercentage = resolutionRate,
            badges = badges
        )
    }
}
