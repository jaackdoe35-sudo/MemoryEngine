package com.memoryengine.app.data.remote.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.memoryengine.app.data.remote.api.GeminiApiService
import com.memoryengine.app.data.remote.model.GeminiRequest

class GeminiAiService(
    private val apiService: GeminiApiService,
    private val apiKey: String = ""
) : AiService {

    private val gson = Gson()

    override suspend fun generateAssociation(text: String): String {
        if (apiKey.isBlank()) {
            return fallbackAssociation(text)
        }
        val prompt = "Analyze the following text and identify key concepts or associations. Formulate a concise summary of relationships:\n\n$text"
        return try {
            val response = apiService.generateContent(apiKey, GeminiRequest.create(prompt))
            response.firstText?.trim() ?: fallbackAssociation(text)
        } catch (e: Exception) {
            fallbackAssociation(text)
        }
    }

    override suspend fun generateRecallPrompt(text: String): RecallPromptData {
        if (apiKey.isBlank()) {
            return fallbackRecallPrompt(text)
        }
        val prompt = "Based on the following text, create a high-yield flashcard for active recall. Return ONLY a JSON object with keys \"question\" and \"answer\". Do not include extra commentary.\n\n$text"
        return try {
            val response = apiService.generateContent(apiKey, GeminiRequest.create(prompt))
            val rawText = response.firstText?.trim() ?: return fallbackRecallPrompt(text)
            parseRecallPromptJson(rawText, text)
        } catch (e: Exception) {
            fallbackRecallPrompt(text)
        }
    }

    override suspend fun generateAnswer(text: String): String {
        if (apiKey.isBlank()) {
            return fallbackAnswer(text)
        }
        val prompt = "Provide a clear, concise answer or explanation for the following content:\n\n$text"
        return try {
            val response = apiService.generateContent(apiKey, GeminiRequest.create(prompt))
            response.firstText?.trim() ?: fallbackAnswer(text)
        } catch (e: Exception) {
            fallbackAnswer(text)
        }
    }

    private fun parseRecallPromptJson(rawText: String, originalText: String): RecallPromptData {
        val cleanJson = rawText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        return try {
            val jsonObject = gson.fromJson(cleanJson, JsonObject::class.java)
            val question = jsonObject.get("question")?.asString
            val answer = jsonObject.get("answer")?.asString
            if (!question.isNullOrBlank() && !answer.isNullOrBlank()) {
                RecallPromptData(question, answer)
            } else {
                fallbackRecallPrompt(originalText)
            }
        } catch (e: Exception) {
            // Fallback parsing for "Question: ... Answer: ..." pattern
            if (rawText.contains("Question:", ignoreCase = true) && rawText.contains("Answer:", ignoreCase = true)) {
                val qIndex = rawText.indexOf("Question:", ignoreCase = true)
                val aIndex = rawText.indexOf("Answer:", ignoreCase = true)
                if (qIndex < aIndex) {
                    val qText = rawText.substring(qIndex + 9, aIndex).trim()
                    val aText = rawText.substring(aIndex + 7).trim()
                    return RecallPromptData(qText, aText)
                }
            }
            fallbackRecallPrompt(originalText)
        }
    }

    private fun fallbackAssociation(text: String): String {
        val snippet = text.take(50).replace("\n", " ")
        return "Conceptual link associated with: '$snippet...'"
    }

    private fun fallbackRecallPrompt(text: String): RecallPromptData {
        val snippet = text.take(60).replace("\n", " ")
        return RecallPromptData(
            question = "What is the key takeaway from '$snippet...'?",
            answer = text.take(200)
        )
    }

    private fun fallbackAnswer(text: String): String {
        return "Generated answer for input: ${text.take(100)}"
    }
}
