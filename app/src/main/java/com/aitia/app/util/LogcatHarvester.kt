package com.aitia.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object LogcatHarvester {

    suspend fun harvestRecentLogs(maxLines: Int = 100, filterTag: String? = null): String = withContext(Dispatchers.IO) {
        runCatching {
            val cmd = buildList {
                add("logcat")
                add("-d")
                add("-v")
                add("time")
                add("-t")
                add(maxLines.toString())
                if (!filterTag.isNullOrBlank()) {
                    add("$filterTag:*")
                    add("*:S")
                }
            }

            val process = Runtime.getRuntime().exec(cmd.toTypedArray())
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val lines = mutableListOf<String>()
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.isNotBlank() && !line.startsWith("--------- beginning of")) {
                    lines.add(line)
                }
                line = reader.readLine()
            }
            process.waitFor()

            if (lines.isEmpty()) {
                "// No logcat entries found for current process or filter."
            } else {
                lines.takeLast(maxLines).joinToString("\n")
            }
        }.getOrElse { error ->
            "// Failed to harvest logcat: ${error.localizedMessage}\n// Note: On Android 13+, system-wide logcat requires READ_LOGS permission via ADB or debug app build."
        }
    }
}
