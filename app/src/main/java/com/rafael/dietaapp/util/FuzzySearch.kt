package com.rafael.dietaapp.util

object FuzzySearch {
    private fun distanciaLevenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1].lowercaseChar() == s2[j - 1].lowercaseChar()) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }

    fun coincide(query: String, texto: String): Boolean {
        if (query.isBlank()) return true
        val q = query.trim().lowercase()
        val t = texto.trim().lowercase()
        
        if (t.contains(q)) return true
        
        val palabrasTexto = t.split("\\s+".toRegex())
        val palabrasQuery = q.split("\\s+".toRegex())
        
        return palabrasQuery.all { pq ->
            palabrasTexto.any { pt ->
                val distancia = distanciaLevenshtein(pq, pt)
                val umbral = when {
                    pq.length <= 3 -> 0
                    pq.length <= 5 -> 1
                    else -> 2
                }
                distancia <= umbral
            }
        }
    }
}
