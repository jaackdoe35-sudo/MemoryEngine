package com.memoryengine.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "associations",
    foreignKeys = [
        ForeignKey(
            entity = MemoryChunk::class,
            parentColumns = ["id"],
            childColumns = ["sourceChunkId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MemoryChunk::class,
            parentColumns = ["id"],
            childColumns = ["targetChunkId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sourceChunkId"]),
        Index(value = ["targetChunkId"])
    ]
)
data class Association(
    @PrimaryKey
    val id: String,
    val sourceChunkId: String,
    val targetChunkId: String,
    val relationshipType: String,
    val strength: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis()
)
