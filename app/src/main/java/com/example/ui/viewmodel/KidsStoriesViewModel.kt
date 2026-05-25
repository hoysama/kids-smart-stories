package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.QuizQuestion
import com.example.data.api.RetrofitClient
import com.example.data.local.AppDatabase
import com.example.data.local.StoryEntity
import com.example.data.repository.StoryRepository
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GenerationState {
    object Idle : GenerationState
    object Loading : GenerationState
    data class Success(val story: StoryEntity) : GenerationState
    data class Error(val message: String) : GenerationState
}

class KidsStoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = StoryRepository(db.storyDao())

    val allStories: StateFlow<List<StoryEntity>> = repository.allStories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _selectedStory = MutableStateFlow<StoryEntity?>(null)
    val selectedStory: StateFlow<StoryEntity?> = _selectedStory.asStateFlow()

    // Quiz score states: maps question index to selected option index
    private val _quizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizAnswers: StateFlow<Map<Int, Int>> = _quizAnswers.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    fun selectStory(story: StoryEntity?) {
        _selectedStory.value = story
        // Reset quiz for this story
        _quizAnswers.value = emptyMap()
        _quizCompleted.value = false
    }

    fun deleteStory(id: Int) {
        viewModelScope.launch {
            if (_selectedStory.value?.id == id) {
                _selectedStory.value = null
            }
            repository.deleteStoryById(id)
        }
    }

    fun answerQuizQuestion(questionIndex: Int, optionIndex: Int) {
        val current = _quizAnswers.value.toMutableMap()
        current[questionIndex] = optionIndex
        _quizAnswers.value = current
    }

    fun submitQuiz() {
        _quizCompleted.value = true
    }

    fun resetQuiz() {
        _quizAnswers.value = emptyMap()
        _quizCompleted.value = false
    }

    fun generateStory(
        heroName: String,
        heroType: String,
        theme: String,
        setting: String,
        tone: String,
        language: String
    ) {
        viewModelScope.launch {
            _generationState.value = GenerationState.Loading
            try {
                val inputName = if (heroName.isBlank()) {
                    if (language == "ar") "البطل الصغير" else "The Little Hero"
                } else heroName

                val newStory = repository.generateAndSaveStory(
                    heroName = inputName,
                    heroType = heroType,
                    theme = theme,
                    setting = setting,
                    tone = tone,
                    language = language
                )
                _generationState.value = GenerationState.Success(newStory)
                selectStory(newStory)
            } catch (e: Throwable) {
                e.printStackTrace()
                _generationState.value = GenerationState.Error(e.message ?: "Unknown error while crafting story")
            }
        }
    }

    fun resetGenerationState() {
        _generationState.value = GenerationState.Idle
    }

    fun getQuizQuestionsForSelectedStory(): List<QuizQuestion> {
        val story = _selectedStory.value ?: return emptyList()
        return try {
            val type = Types.newParameterizedType(List::class.java, QuizQuestion::class.java)
            val adapter = RetrofitClient.moshiParser.adapter<List<QuizQuestion>>(type)
            adapter.fromJson(story.quizJson) ?: emptyList()
        } catch (e: Throwable) {
            emptyList()
        }
    }
}
