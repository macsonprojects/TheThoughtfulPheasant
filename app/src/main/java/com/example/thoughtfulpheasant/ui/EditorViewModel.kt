package com.example.thoughtfulpheasant.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thoughtfulpheasant.data.MoodCategory
import com.example.thoughtfulpheasant.data.MoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MoodRepository(application)

    val moodCategories: StateFlow<List<MoodCategory>> = repository.moodCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(MoodCategory(name, emptyList()))
        }
    }

    fun deleteCategory(name: String) {
        viewModelScope.launch {
            repository.deleteCategory(name)
        }
    }

    fun updatePhrases(categoryName: String, phrases: List<String>) {
        viewModelScope.launch {
            val current = moodCategories.value.find { it.name == categoryName } ?: return@launch
            repository.updateCategory(categoryName, current.copy(phrases = phrases))
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            val current = moodCategories.value.find { it.name == oldName } ?: return@launch
            repository.updateCategory(oldName, current.copy(name = newName))
        }
    }
}
