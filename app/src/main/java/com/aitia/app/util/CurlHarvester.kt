package com.aitia.app.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

data class ParsedHttpRequest(
    val method: String = "GET",
    val url: String = "",
    val headers: Map<String, String> = emptyMap(),
    val body: String = "",
    val formattedJson: String = ""
)

object CurlHarvester {

    private val jsonPretty = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun parseAndGenerateCurl(rawInput: String): Pair<ParsedHttpRequest, String> {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) {
            return ParsedHttpRequest() to "curl"
        }

        // If user already pasted a curl command, extract components or clean it
        if (trimmed.startsWith("curl ", ignoreCase = true)) {
            val parsed = parseCurlCommand(trimmed)
            return parsed to trimmed
        }

        // Parse raw HTTP text (e.g. "POST /v1/users HTTP/1.1\nHost: api.example.com\n...")
        val lines = trimmed.lines()
        var method = "GET"
        var url = ""
        val headers = mutableMapOf<String, String>()
        val bodyBuilder = StringBuilder()
        var parsingBody = false

        val firstLine = lines.firstOrNull()?.trim() ?: ""
        val firstLineParts = firstLine.split("\\s+".toRegex())

        val isHttpHeaderStart = firstLineParts.size >= 2 && firstLineParts[0].uppercase() in listOf(
            "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"
        )

        if (isHttpHeaderStart) {
            method = firstLineParts[0].uppercase()
            url = firstLineParts[1]
            for (i in 1 until lines.size) {
                val line = lines[i]
                if (parsingBody) {
                    bodyBuilder.appendLine(line)
                } else {
                    if (line.isBlank()) {
                        parsingBody = true
                    } else {
                        val colonIndex = line.indexOf(':')
                        if (colonIndex > 0) {
                            val headerKey = line.substring(0, colonIndex).trim()
                            val headerValue = line.substring(colonIndex + 1).trim()
                            headers[headerKey] = headerValue
                        }
                    }
                }
            }
            if (headers.containsKey("Host") && !url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://${headers["Host"]}$url"
            }
        } else if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            url = trimmed.lines().first()
            if (lines.size > 1) {
                bodyBuilder.append(lines.drop(1).joinToString("\n"))
                method = "POST"
            }
        } else {
            // Raw JSON body or general text
            bodyBuilder.append(trimmed)
            method = "POST"
        }

        val body = bodyBuilder.toString().trim()
        val formattedJson = runCatching {
            if (body.startsWith("{") || body.startsWith("[")) {
                val element = Json.parseToJsonElement(body)
                jsonPretty.encodeToString(JsonElement.serializer(), element)
            } else ""
        }.getOrDefault(body)

        val parsed = ParsedHttpRequest(
            method = method,
            url = url.ifEmpty { "https://api.example.com/endpoint" },
            headers = headers,
            body = body,
            formattedJson = formattedJson
        )

        val curl = buildCurl(parsed)
        return parsed to curl
    }

    private fun parseCurlCommand(curlCmd: String): ParsedHttpRequest {
        var method = "GET"
        var url = ""
        val headers = mutableMapOf<String, String>()
        var body = ""

        val tokens = curlCmd.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i].trim().removeSurrounding("\"").removeSurrounding("'")
            when {
                token == "-X" && i + 1 < tokens.size -> {
                    method = tokens[i + 1].removeSurrounding("\"").removeSurrounding("'").uppercase()
                    i++
                }
                (token == "-H" || token == "--header") && i + 1 < tokens.size -> {
                    val headerStr = tokens[i + 1].removeSurrounding("\"").removeSurrounding("'")
                    val idx = headerStr.indexOf(':')
                    if (idx > 0) {
                        headers[headerStr.substring(0, idx).trim()] = headerStr.substring(idx + 1).trim()
                    }
                    i++
                }
                (token == "-d" || token == "--data" || token == "--data-raw") && i + 1 < tokens.size -> {
                    body = tokens[i + 1].removeSurrounding("\"").removeSurrounding("'")
                    if (method == "GET") method = "POST"
                    i++
                }
                token.startsWith("http://") || token.startsWith("https://") -> {
                    url = token
                }
            }
            i++
        }

        val formattedJson = runCatching {
            if (body.startsWith("{") || body.startsWith("[")) {
                val element = Json.parseToJsonElement(body)
                jsonPretty.encodeToString(JsonElement.serializer(), element)
            } else ""
        }.getOrDefault(body)

        return ParsedHttpRequest(
            method = method,
            url = url,
            headers = headers,
            body = body,
            formattedJson = formattedJson
        )
    }

    fun buildCurl(req: ParsedHttpRequest): String {
        return buildString {
            append("curl -X ${req.method} \"${req.url}\"")
            req.headers.forEach { (k, v) ->
                append(" \\\n  -H \"$k: $v\"")
            }
            if (req.body.isNotBlank()) {
                val escapedBody = req.body.replace("\"", "\\\"").replace("\n", "")
                append(" \\\n  -d \"$escapedBody\"")
            }
        }
    }
}
