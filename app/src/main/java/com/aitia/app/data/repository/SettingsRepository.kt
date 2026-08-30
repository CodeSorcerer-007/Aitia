package com.aitia.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SettingsRepository(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "aitia_secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getGeminiApiKey(): String {
        return sharedPreferences.getString("gemini_api_key", "") ?: ""
    }

    fun setGeminiApiKey(key: String) {
        sharedPreferences.edit().putString("gemini_api_key", key).apply()
    }

    fun getGithubPat(): String {
        return sharedPreferences.getString("github_pat", "") ?: ""
    }

    fun setGithubPat(pat: String) {
        sharedPreferences.edit().putString("github_pat", pat).apply()
    }

    fun getDefaultRepo(): String {
        return sharedPreferences.getString("default_repo", "CodeSorcerer-007/Aitia") ?: "CodeSorcerer-007/Aitia"
    }

    fun setDefaultRepo(repo: String) {
        sharedPreferences.edit().putString("default_repo", repo).apply()
    }
}
