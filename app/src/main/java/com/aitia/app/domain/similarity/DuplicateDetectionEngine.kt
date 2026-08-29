package com.aitia.app.domain.similarity

import com.aitia.app.domain.model.Issue

data class DuplicateMatch(
    val issue: Issue,
    val similarityScore: Float,
    val matchedReason: String
)

object DuplicateDetectionEngine {

    private val STOP_WORDS = setOf(
        "the", "a", "an", "and", "or", "in", "on", "at", "to", "for", "with",
        "of", "by", "from", "is", "are", "was", "were", "app", "application",
        "bug", "crash", "error", "when", "while", "during"
    )

    fun findDuplicates(
        queryTitle: String,
        existingIssues: List<Issue>,
        threshold: Float = 0.40f
    ): List<DuplicateMatch> {
        val queryTokens = tokenize(queryTitle)
        if (queryTokens.isEmpty()) return emptyList()

        val results = mutableListOf<DuplicateMatch>()

        for (candidate in existingIssues) {
            val candidateTokens = tokenize(candidate.title)
            if (candidateTokens.isEmpty()) continue

            // 1. Jaccard token similarity
            val intersectionSize = queryTokens.intersect(candidateTokens).size
            val unionSize = queryTokens.union(candidateTokens).size
            val tokenSimilarity = if (unionSize > 0) intersectionSize.toFloat() / unionSize.toFloat() else 0f

            // 2. Character 3-gram similarity for typos / slight variations
            val nGramSim = computeNGramSimilarity(queryTitle.lowercase(), candidate.title.lowercase(), 3)

            // Combined weighted score
            val combinedScore = (tokenSimilarity * 0.65f) + (nGramSim * 0.35f)

            if (combinedScore >= threshold) {
                val reason = when {
                    tokenSimilarity >= 0.70f -> "High word overlap with existing issue"
                    nGramSim >= 0.60f -> "Similar phrasing and structure"
                    else -> "Possible related defect in same area"
                }
                results.add(
                    DuplicateMatch(
                        issue = candidate,
                        similarityScore = combinedScore,
                        matchedReason = reason
                    )
                )
            }
        }

        return results.sortedByDescending { it.similarityScore }.take(3)
    }

    private fun tokenize(text: String): Set<String> {
        return text.lowercase()
            .split(Regex("[^a-zA-Z0-9_]+"))
            .filter { it.length > 2 && it !in STOP_WORDS }
            .toSet()
    }

    private fun computeNGramSimilarity(s1: String, s2: String, n: Int): Float {
        val ngrams1 = getNgrams(s1, n)
        val ngrams2 = getNgrams(s2, n)
        if (ngrams1.isEmpty() || ngrams2.isEmpty()) return 0f

        val intersection = ngrams1.intersect(ngrams2).size
        val union = ngrams1.union(ngrams2).size
        return if (union > 0) intersection.toFloat() / union.toFloat() else 0f
    }

    private fun getNgrams(s: String, n: Int): Set<String> {
        val clean = s.replace("\\s+".toRegex(), " ").trim()
        if (clean.length < n) return setOf(clean)
        val grams = mutableSetOf<String>()
        for (i in 0..clean.length - n) {
            grams.add(clean.substring(i, i + n))
        }
        return grams
    }
}
