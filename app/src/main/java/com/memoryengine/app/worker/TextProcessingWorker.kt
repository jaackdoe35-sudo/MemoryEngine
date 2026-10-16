package com.memoryengine.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.memoryengine.app.data.local.database.AppDatabase
import com.memoryengine.app.data.local.entity.Association
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.data.remote.api.GeminiApiService
import com.memoryengine.app.data.remote.service.AiService
import com.memoryengine.app.data.remote.service.GeminiAiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class TextProcessingWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val customAiService: AiService? = null,
    private val customDatabase: AppDatabase? = null
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_DOCUMENT_ID = "document_id"
        const val KEY_DOCUMENT_TEXT = "document_text"
        const val KEY_PROCESSED_CHUNKS_COUNT = "processed_chunks_count"
    }

    override suspend fun doWork(): Result {
        val documentId = inputData.getString(KEY_DOCUMENT_ID) ?: return Result.failure()
        val text = inputData.getString(KEY_DOCUMENT_TEXT) ?: return Result.failure()

        val db = customDatabase ?: AppDatabase.getInstance(applicationContext)
        val aiService = customAiService ?: createDefaultAiService()

        val chunkTexts = TextChunker.chunkText(text)
        val chunks = mutableListOf<MemoryChunk>()

        for ((index, chunkText) in chunkTexts.withIndex()) {
            val chunkId = UUID.randomUUID().toString()
            val wordCount = chunkText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            val chunk = MemoryChunk(
                id = chunkId,
                documentId = documentId,
                content = chunkText,
                orderIndex = index,
                wordCount = wordCount
            )
            chunks.add(chunk)
            db.memoryChunkDao().insertChunk(chunk)

            // AI Processing: Recall Prompt Generation
            val recallPromptData = aiService.generateRecallPrompt(chunkText)
            val prompt = RecallPrompt(
                id = UUID.randomUUID().toString(),
                chunkId = chunkId,
                question = recallPromptData.question,
                answer = recallPromptData.answer
            )
            db.recallPromptDao().insertRecallPrompt(prompt)

            // AI Processing: Association Generation
            val associationDesc = aiService.generateAssociation(chunkText)
            if (index > 0) {
                val previousChunk = chunks[index - 1]
                val association = Association(
                    id = UUID.randomUUID().toString(),
                    sourceChunkId = previousChunk.id,
                    targetChunkId = chunkId,
                    relationshipType = associationDesc
                )
                db.associationDao().insertAssociation(association)
            }
        }

        val existingDoc = db.documentDao().getDocumentById(documentId)
        if (existingDoc != null) {
            db.documentDao().updateDocument(
                existingDoc.copy(chunkCount = chunks.size)
            )
        }

        val outputData = workDataOf(KEY_PROCESSED_CHUNKS_COUNT to chunks.size)
        return Result.success(outputData)
    }

    private fun createDefaultAiService(): AiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(GeminiApiService::class.java)
        return GeminiAiService(apiService = apiService)
    }
}
