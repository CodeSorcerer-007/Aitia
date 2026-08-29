package com.aitia.app

import com.aitia.app.util.CurlHarvester
import com.aitia.app.util.ParsedHttpRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurlHarvesterTest {

    @Test
    fun testBuildCurlFromParsedRequest() {
        val req = ParsedHttpRequest(
            method = "POST",
            url = "https://api.aitia.dev/v1/issues",
            headers = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer token123"
            ),
            body = """{"title":"Camera crash"}"""
        )

        val curl = CurlHarvester.buildCurl(req)

        assertTrue(curl.startsWith("curl -X POST \"https://api.aitia.dev/v1/issues\""))
        assertTrue(curl.contains("-H \"Content-Type: application/json\""))
        assertTrue(curl.contains("-H \"Authorization: Bearer token123\""))
        assertTrue(curl.contains("-d \"{\\\"title\\\":\\\"Camera crash\\\"}\""))
    }

    @Test
    fun testParseRawJsonGeneratesCurlAndFormattedJson() {
        val rawJson = """{"id":101,"status":"OPEN","title":"Null pointer"}"""
        val (parsed, curl) = CurlHarvester.parseAndGenerateCurl(rawJson)

        assertTrue(parsed.formattedJson.contains("\n"))
        assertTrue(parsed.formattedJson.contains("  \"id\": 101"))
        assertTrue(parsed.formattedJson.contains("  \"status\": \"OPEN\""))
        assertTrue(curl.contains("curl -X POST"))
    }

    @Test
    fun testParseRawCurlCommand() {
        val rawCurl = "curl -X GET https://api.aitia.dev/health -H \"Accept: application/json\""
        val (parsed, _) = CurlHarvester.parseAndGenerateCurl(rawCurl)

        assertEquals("GET", parsed.method)
        assertEquals("https://api.aitia.dev/health", parsed.url)
        assertEquals("application/json", parsed.headers["Accept"])
    }
}
