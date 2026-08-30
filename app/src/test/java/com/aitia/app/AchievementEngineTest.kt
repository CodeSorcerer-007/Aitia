package com.aitia.app

import com.aitia.app.domain.gamification.AchievementEngine
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementEngineTest {

    @Test
    fun testComputeProfileCalculatesLevelAndBadges() {
        val issues = listOf(
            Issue(
                id = 1,
                title = "Fixed crash 1",
                type = IssueType.CRASH,
                priority = Priority.HIGH,
                status = IssueStatus.FIXED,
                solution = "Added null checks",
                technicalDetails = "java.lang.NullPointerException",
                stepsToReproduce = "1. Open app"
            ),
            Issue(
                id = 2,
                title = "Fixed crash 2",
                type = IssueType.BUG,
                priority = Priority.MEDIUM,
                status = IssueStatus.VERIFIED,
                solution = "Fixed layout padding"
            ),
            Issue(
                id = 3,
                title = "Open UI bug",
                type = IssueType.UI_UX,
                priority = Priority.LOW,
                status = IssueStatus.OPEN
            )
        )

        val profile = AchievementEngine.computeProfile(issues)

        assertNotNull(profile)
        assertEquals(2, profile.totalBugsSquashed)
        assertEquals(66, profile.resolutionRatePercentage)
        assertTrue(profile.totalXp > 0)
        assertTrue(profile.badges.isNotEmpty())

        val firstBlood = profile.badges.first { it.id == "first_blood" }
        assertTrue(firstBlood.isUnlocked)

        val speedDemon = profile.badges.first { it.id == "speed_demon" }
        assertTrue(speedDemon.isUnlocked)
    }
}
