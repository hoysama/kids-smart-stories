package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "temperature") val temperature: Double? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

// The story data structure we want Gemini to generate inside response text:
@JsonClass(generateAdapter = true)
data class QuizQuestion(
    @Json(name = "question") val question: String,
    @Json(name = "options") val options: List<String>,
    @Json(name = "correctOptionIndex") val correctOptionIndex: Int
)

@JsonClass(generateAdapter = true)
data class GeminiStoryResponse(
    @Json(name = "title") val title: String,
    @Json(name = "storyContent") val storyContent: String,
    @Json(name = "moral") val moral: String,
    @Json(name = "quiz") val quiz: List<QuizQuestion>
)
