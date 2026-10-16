package com.memoryengine.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "memory_chunks",
    foreignKeys = [
        ForeignKey(
            entity = Document::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["documentId"])]
)
data class MemoryChunk(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val content: String,
    val orderIndex: Int,
    val wordCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
