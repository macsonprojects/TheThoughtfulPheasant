package com.example.thoughtfulpheasant.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mood_settings")

class MoodRepository(private val context: Context) {

    private val moodCategoriesKey = stringPreferencesKey("mood_categories")

    val moodCategories: Flow<List<MoodCategory>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[moodCategoriesKey]
            if (json != null) {
                try {
                    Json.decodeFromString<List<MoodCategory>>(json)
                } catch (_: Exception) {
                    defaultMoodCategories
                }
            } else {
                defaultMoodCategories
            }
        }

    suspend fun addCategory(category: MoodCategory) {
        context.dataStore.edit { preferences ->
            val current = getCurrentCategories(preferences)
            preferences[moodCategoriesKey] = Json.encodeToString(current + category)
        }
    }

    suspend fun updateCategory(oldName: String, newCategory: MoodCategory) {
        context.dataStore.edit { preferences ->
            val current = getCurrentCategories(preferences)
            val updated = current.map { if (it.name == oldName) newCategory else it }
            preferences[moodCategoriesKey] = Json.encodeToString(updated)
        }
    }

    suspend fun deleteCategory(categoryName: String) {
        context.dataStore.edit { preferences ->
            val current = getCurrentCategories(preferences)
            val updated = current.filter { it.name != categoryName }
            preferences[moodCategoriesKey] = Json.encodeToString(updated)
        }
    }

    private fun getCurrentCategories(preferences: Preferences): List<MoodCategory> {
        val json = preferences[moodCategoriesKey] ?: return defaultMoodCategories
        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            defaultMoodCategories
        }
    }
}
