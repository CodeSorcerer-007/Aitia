package com.aitia.app

import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.similarity.DuplicateDetectionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateDetectionEngineTest {

    @Test
    fun testFindsDuplicateWhenTitlesAreSimilar() {
        val existingIssues = listOf(
            Issue(id = 1, title = "Camera crashes from profile screen"),
            Issue(id = 2, title = "Login button does not respond on click"),
            Issue(id = 3, title = "Dark theme background color contrast glitch")
        )

        val query = "Camera crashes when opening profile camera"
        val matches = DuplicateDetectionEngine.findDuplicates(query, existingIssues)

        assertTrue(matches.isNotEmpty())
        assertEquals(1L, matches.first().issue.id)
    }

    @Test
    fun testNoDuplicateWhenQueryIsUnique() {
        val existingIssues = listOf(
            Issue(id = 1, title = "Camera crashes from profile screen"),
            Issue(id = 2, title = "Login button does not respond on click")
        )

        val query = "Database migration foreign key failure"
        val matches = DuplicateDetectionEngine.findDuplicates(query, existingIssues)

        assertTrue(matches.isEmpty())
    }
}
