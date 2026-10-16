package com.memoryengine.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey
    val id: String,
    val title: String,
    val sourcePath: String?,
    val mimeType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val chunkCount: Int = 0
)
