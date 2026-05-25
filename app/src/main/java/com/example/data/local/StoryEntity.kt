package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val heroName: String,
    val heroType: String,
    val theme: String,
    val setting: String,
    val tone: String,
    val storyContent: String,
    val moral: String,
    val quizJson: String, // Stringified JSON representation of quiz questions
    val createdAt: Long = System.currentTimeMillis(),
    val language: String = "ar"
)
