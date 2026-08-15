package com.agon.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "salon_prefs")

class Prefs(private val context: Context) {
    private val haptic = booleanPreferencesKey("haptic")
    private val language = stringPreferencesKey("language")
    private val palette = stringPreferencesKey("palette")
    private val darkMode = stringPreferencesKey("dark_mode")
    private val autoplay = booleanPreferencesKey("autoplay")
    private val notify = booleanPreferencesKey("notify")
    private val onboarding = booleanPreferencesKey("onboarding")
    private val registered = booleanPreferencesKey("registered")
    private val sessionId = stringPreferencesKey("session_id")

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            haptic = p[haptic] ?: true,
            language = p[language] ?: "en",
            palette = p[palette] ?: "LEONE_FLAG",
            darkMode = p[darkMode] ?: "system",
            autoplayVoice = p[autoplay] ?: false,
            notifyMessages = p[notify] ?: true,
            onboardingDone = p[onboarding] ?: false,
            registered = p[registered] ?: false,
        )
    }

    val sessionUserId: Flow<String> = context.dataStore.data.map { it[sessionId].orEmpty() }

    suspend fun setHaptic(value: Boolean) = context.dataStore.edit { it[haptic] = value }
    suspend fun setLanguage(value: String) = context.dataStore.edit { it[language] = value }
    suspend fun setPalette(value: String) = context.dataStore.edit { it[palette] = value }
    suspend fun setDarkMode(value: String) = context.dataStore.edit { it[darkMode] = value }
    suspend fun setAutoplay(value: Boolean) = context.dataStore.edit { it[autoplay] = value }
    suspend fun setNotify(value: Boolean) = context.dataStore.edit { it[notify] = value }
    suspend fun setOnboardingDone() = context.dataStore.edit { it[onboarding] = true }
    suspend fun setRegistered(value: Boolean) = context.dataStore.edit { it[registered] = value }
    suspend fun setSession(userId: String) = context.dataStore.edit { it[sessionId] = userId }
    suspend fun clearSession() = context.dataStore.edit {
        it[sessionId] = ""
        it[registered] = false
    }
}
