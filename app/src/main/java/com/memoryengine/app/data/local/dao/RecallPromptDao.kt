package com.memoryengine.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.memoryengine.app.data.local.entity.RecallPrompt
import kotlinx.coroutines.flow.Flow

@Dao
interface RecallPromptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecallPrompt(prompt: RecallPrompt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecallPrompts(prompts: List<RecallPrompt>)

    @Query("SELECT * FROM recall_prompts WHERE id = :id")
    suspend fun getPromptById(id: String): RecallPrompt?

    @Query("SELECT * FROM recall_prompts WHERE chunkId = :chunkId")
    suspend fun getPromptsForChunk(chunkId: String): List<RecallPrompt>

    @Query("SELECT * FROM recall_prompts WHERE nextReviewAt <= :currentTimeMillis ORDER BY nextReviewAt ASC")
    suspend fun getPromptsDueForReview(currentTimeMillis: Long = System.currentTimeMillis()): List<RecallPrompt>

    @Query("SELECT * FROM recall_prompts ORDER BY nextReviewAt ASC")
    fun getAllPrompts(): Flow<List<RecallPrompt>>

    @Update
    suspend fun updateRecallPrompt(prompt: RecallPrompt)

    @Query("DELETE FROM recall_prompts WHERE id = :id")
    suspend fun deletePrompt(id: String)
}
