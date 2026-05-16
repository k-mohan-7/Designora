package com.example.interiordesign_ai.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "interio_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID       = intPreferencesKey("user_id")
        private val KEY_USER_NAME     = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL    = stringPreferencesKey("user_email")
        private val KEY_USER_PHONE    = stringPreferencesKey("user_phone")
        private val KEY_PROFILE_IMAGE = stringPreferencesKey("profile_image_path")
    }

    suspend fun saveSession(userId: Int, name: String, email: String, phone: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID]    = userId
            prefs[KEY_USER_NAME]  = name
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_PHONE] = phone
        }
    }

    suspend fun saveProfileImagePath(path: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PROFILE_IMAGE] = path
        }
    }

    suspend fun getProfileImagePath(): String =
        context.dataStore.data.map { it[KEY_PROFILE_IMAGE] ?: "" }.first()

    fun profileImagePathFlow(): Flow<String> =
        context.dataStore.data.map { it[KEY_PROFILE_IMAGE] ?: "" }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getUserId(): Int =
        context.dataStore.data.map { it[KEY_USER_ID] ?: 0 }.first()

    suspend fun getUserName(): String =
        context.dataStore.data.map { it[KEY_USER_NAME] ?: "" }.first()

    suspend fun getUserEmail(): String =
        context.dataStore.data.map { it[KEY_USER_EMAIL] ?: "" }.first()

    suspend fun getUserPhone(): String =
        context.dataStore.data.map { it[KEY_USER_PHONE] ?: "" }.first()

    fun isLoggedIn(): Flow<Boolean> =
        context.dataStore.data.map { (it[KEY_USER_ID] ?: 0) > 0 }

    fun userIdFlow(): Flow<Int> =
        context.dataStore.data.map { it[KEY_USER_ID] ?: 0 }

    fun userNameFlow(): Flow<String> =
        context.dataStore.data.map { it[KEY_USER_NAME] ?: "" }
}
