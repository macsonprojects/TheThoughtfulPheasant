package com.example.thoughtfulpheasant.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.thoughtfulpheasant.data.MoodCategory
import com.example.thoughtfulpheasant.data.MoodRepository
import com.example.thoughtfulpheasant.speech.PhraseSpeaker
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GeneratorUiState(
    val isTtsReady: Boolean = false,
    val isMuted: Boolean = false,
    val displayedPhrase: String = "",
    val categoryIndex: Int = 0,
    val activeCategory: MoodCategory? = null,
    val categories: List<MoodCategory> = emptyList()
)

class GeneratorViewModel @JvmOverloads constructor(
    application: Application,
    speakerProvider: ((Boolean) -> Unit) -> PhraseSpeaker = { onReady -> PhraseSpeaker(application, onReady) }
) : AndroidViewModel(application) {

    private val repository = MoodRepository(application)
    private val _uiState = MutableStateFlow(GeneratorUiState(displayedPhrase = application.getString(com.example.thoughtfulpheasant.R.string.initial_phrase)))
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.moodCategories.collect { categories ->
                _uiState.update { state ->
                    val safeIndex = state.categoryIndex.coerceIn(0, (categories.size - 1).coerceAtLeast(0))
                    state.copy(
                        categories = categories,
                        activeCategory = if (categories.isNotEmpty()) categories[safeIndex] else null,
                        categoryIndex = safeIndex
                    )
                }
            }
        }
    }

    private val speaker = speakerProvider { ready ->
        _uiState.update { it.copy(isTtsReady = ready) }
    }

    // Stores shuffled pools for each category to prevent immediate repeats
    private val phrasePools = mutableMapOf<String, MutableList<String>>()

    private fun getNextPhrase(category: MoodCategory): String {
        val pool = phrasePools.getOrPut(category.name) {
            category.phrases.shuffled().toMutableList()
        }

        if (pool.isEmpty()) {
            val newPool = category.phrases.shuffled().toMutableList()
            // Avoid repeating the last phrase immediately if possible
            if (newPool.size > 1 && newPool[0] == _uiState.value.displayedPhrase) {
                val first = newPool.removeAt(0)
                newPool.add(first)
            }
            pool.addAll(newPool)
        }

        return pool.removeAt(0)
    }

    fun onCategoryChange(newIndex: Int) {
        val categories = _uiState.value.categories
        if (categories.isEmpty()) return
        val safeIndex = newIndex.coerceIn(0, categories.size - 1)
        _uiState.update { it.copy(
            categoryIndex = safeIndex,
            activeCategory = categories[safeIndex]
        ) }
    }

    fun onToggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        if (_uiState.value.isMuted) speaker.stop()
    }

    fun onImageTapped() {
        val activeCategory = _uiState.value.activeCategory ?: return
        val phrase = getNextPhrase(activeCategory)
        _uiState.update { it.copy(displayedPhrase = phrase) }
        if (!_uiState.value.isMuted) {
            speaker.speak(phrase)
        }
    }

    override fun onCleared() {
        speaker.release()
    }
}
