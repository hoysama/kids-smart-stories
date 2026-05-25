package com.example.ui.viewmodel

import android.app.Application
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import java.util.Locale

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

    // TTS management
    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            tts = TextToSpeech(application) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    _isSpeaking.value = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tts = null
        }
    }

    fun selectStory(story: StoryEntity?) {
        _selectedStory.value = story
        // Reset quiz for this story
        _quizAnswers.value = emptyMap()
        _quizCompleted.value = false
        stopSpeaking()
    }

    fun deleteStory(id: Int) {
        viewModelScope.launch {
            if (_selectedStory.value?.id == id) {
                _selectedStory.value = null
                stopSpeaking()
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
            } catch (e: Exception) {
                e.printStackTrace()
                _generationState.value = GenerationState.Error(e.message ?: "Unknown error while crafting story")
            }
        }
    }

    fun resetGenerationState() {
        _generationState.value = GenerationState.Idle
    }

    // TTS Functions
    fun speakText(text: String, languageCode: String) {
        tts?.let { ttsInstance ->
            if (ttsInstance.isSpeaking) {
                ttsInstance.stop()
                _isSpeaking.value = false
                return
            }
            val locale = if (languageCode == "ar") Locale("ar") else Locale.US
            ttsInstance.language = locale
            
            _isSpeaking.value = true
            ttsInstance.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "StorySpeechId")
            }
            // Clean text from bold markers or formatting before reading out loud
            val cleanText = text.replace("**", "").replace("_", "")
            ttsInstance.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "StorySpeechId")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun getQuizQuestionsForSelectedStory(): List<QuizQuestion> {
        val story = _selectedStory.value ?: return emptyList()
        return try {
            val type = Types.newParameterizedType(List::class.java, QuizQuestion::class.java)
            val adapter = RetrofitClient.moshiParser.adapter<List<QuizQuestion>>(type)
            adapter.fromJson(story.quizJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}
