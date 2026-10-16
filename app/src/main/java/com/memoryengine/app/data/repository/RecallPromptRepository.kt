package com.memoryengine.app.data.repository

import com.memoryengine.app.data.local.dao.RecallPromptDao
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.domain.scheduler.Rating
import com.memoryengine.app.domain.scheduler.SM2Scheduler
import kotlinx.coroutines.flow.Flow

class RecallPromptRepository(
    private val recallPromptDao: RecallPromptDao,
    private val sm2Scheduler: SM2Scheduler = SM2Scheduler()
) {
    fun getAllPrompts(): Flow<List<RecallPrompt>> = recallPromptDao.getAllPrompts()

    suspend fun getPromptById(id: String): RecallPrompt? = recallPromptDao.getPromptById(id)

    suspend fun getPromptsForChunk(chunkId: String): List<RecallPrompt> =
        recallPromptDao.getPromptsForChunk(chunkId)

    suspend fun getPromptsDueForReview(currentTimeMillis: Long = System.currentTimeMillis()): List<RecallPrompt> =
        recallPromptDao.getPromptsDueForReview(currentTimeMillis)

    suspend fun insertRecallPrompt(prompt: RecallPrompt) =
        recallPromptDao.insertRecallPrompt(prompt)

    suspend fun insertRecallPrompts(prompts: List<RecallPrompt>) =
        recallPromptDao.insertRecallPrompts(prompts)

    suspend fun submitReview(
        promptId: String,
        rating: Rating,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): RecallPrompt? {
        val existingPrompt = recallPromptDao.getPromptById(promptId) ?: return null
        val updatedPrompt = sm2Scheduler.calculateNextReview(existingPrompt, rating, currentTimeMillis)
        recallPromptDao.updateRecallPrompt(updatedPrompt)
        return updatedPrompt
    }
}
