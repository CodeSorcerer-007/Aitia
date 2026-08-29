package com.aitia.app

import com.aitia.app.data.local.Converters
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.RelationshipType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun testTimestampConversion() {
        val now = Instant.now()
        val timestamp = converters.dateToTimestamp(now)
        val convertedBack = converters.fromTimestamp(timestamp)
        assertEquals(now.toEpochMilli(), convertedBack?.toEpochMilli())
    }

    @Test
    fun testEnumConversions() {
        assertEquals(IssueType.CRASH, converters.toIssueType(converters.fromIssueType(IssueType.CRASH)))
        assertEquals(IssueStatus.VERIFIED, converters.toIssueStatus(converters.fromIssueStatus(IssueStatus.VERIFIED)))
        assertEquals(Priority.CRITICAL, converters.toPriority(converters.fromPriority(Priority.CRITICAL)))
        assertEquals(RelationshipType.DUPLICATE_OF, converters.toRelationshipType(converters.fromRelationshipType(RelationshipType.DUPLICATE_OF)))
    }
}
