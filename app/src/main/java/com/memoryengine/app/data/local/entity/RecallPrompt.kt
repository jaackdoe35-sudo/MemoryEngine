package com.memoryengine.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recall_prompts",
    foreignKeys = [
        ForeignKey(
            entity = MemoryChunk::class,
            parentColumns = ["id"],
            childColumns = ["chunkId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chunkId"])]
)
data class RecallPrompt(
    @PrimaryKey
    val id: String,
    val chunkId: String,
    val question: String,
    val answer: String,
    val intervalDays: Int = 1,
    val easeFactor: Float = 2.5f,
    val nextReviewAt: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0
)
