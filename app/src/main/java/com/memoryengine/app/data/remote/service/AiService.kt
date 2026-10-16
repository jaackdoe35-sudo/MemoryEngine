package com.memoryengine.app.data.remote.service

data class RecallPromptData(
    val question: String,
    val answer: String
)

interface AiService {
    suspend fun generateAssociation(text: String): String
    suspend fun generateRecallPrompt(text: String): RecallPromptData
    suspend fun generateAnswer(text: String): String
}
