package com.aitia.app.domain.similarity

import com.aitia.app.domain.model.Issue

data class MatchedFix(
    val issue: Issue,
    val similarityScore: Float,
    val matchReason: String
)

object PreviousFixMatcher {

    fun findSimilarResolvedFixes(
        currentIssue: Issue,
        allIssues: List<Issue>,
        threshold: Float = 0.35f,
        maxResults: Int = 3
    ): List<MatchedFix> {
        val resolvedIssues = allIssues.filter { 
            it.id != currentIssue.id && 
            it.isResolved && 
            (it.solution.isNotBlank() || it.suspectedCause.isNotBlank())
        }

        if (resolvedIssues.isEmpty()) return emptyList()

        val results = mutableListOf<MatchedFix>()

        val currentTokens = tokenize("${currentIssue.title} ${currentIssue.errorMessage} ${currentIssue.exceptionType} ${currentIssue.sourceFile}")

        for (candidate in resolvedIssues) {
            var score = 0.0f
            val reasons = mutableListOf<String>()

            // 1. Exception Type exact match (+45%)
            if (currentIssue.exceptionType.isNotBlank() && 
                candidate.exceptionType.equals(currentIssue.exceptionType, ignoreCase = true)) {
                score += 0.45f
                reasons.add("Exact Exception Match (${currentIssue.exceptionType})")
            }

            // 2. Source File match (+30%)
            if (currentIssue.sourceFile.isNotBlank() && 
                candidate.sourceFile.equals(currentIssue.sourceFile, ignoreCase = true)) {
                score += 0.30f
                reasons.add("Same Source File (${currentIssue.sourceFile})")
            }

            // 3. Token Overlap
            val candidateTokens = tokenize("${candidate.title} ${candidate.errorMessage} ${candidate.exceptionType} ${candidate.sourceFile}")
            val overlap = currentTokens.intersect(candidateTokens)
            if (currentTokens.isNotEmpty() && candidateTokens.isNotEmpty()) {
                val jaccard = overlap.size.toFloat() / (currentTokens.union(candidateTokens).size.toFloat())
                score += jaccard * 0.40f
                if (overlap.isNotEmpty()) {
                    reasons.add("Matching keywords: ${overlap.take(3).joinToString(", ")}")
                }
            }

            // 4. Same project bonus (+10%)
            if (currentIssue.projectId != null && currentIssue.projectId == candidate.projectId) {
                score += 0.10f
            }

            val finalScore = score.coerceIn(0.0f, 1.0f)
            if (finalScore >= threshold) {
                results.add(
                    MatchedFix(
                        issue = candidate,
                        similarityScore = finalScore,
                        matchReason = reasons.joinToString(" · ")
                    )
                )
            }
        }

        return results.sortedByDescending { it.similarityScore }.take(maxResults)
    }

    private fun tokenize(text: String): Set<String> {
        val stopWords = setOf("the", "a", "an", "and", "or", "in", "on", "at", "to", "for", "with", "is", "was", "null", "app", "error", "exception")
        return text.lowercase()
            .split("[^a-zA-Z0-9_.]+".toRegex())
            .filter { it.length > 2 && it !in stopWords }
            .toSet()
    }
}
