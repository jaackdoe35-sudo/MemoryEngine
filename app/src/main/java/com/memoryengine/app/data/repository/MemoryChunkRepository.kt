package com.memoryengine.app.data.repository

import com.memoryengine.app.data.local.dao.MemoryChunkDao
import com.memoryengine.app.data.local.entity.MemoryChunk
import kotlinx.coroutines.flow.Flow

class MemoryChunkRepository(
    private val chunkDao: MemoryChunkDao
) {
    suspend fun insertChunk(chunk: MemoryChunk) = chunkDao.insertChunk(chunk)

    suspend fun insertChunks(chunks: List<MemoryChunk>) = chunkDao.insertChunks(chunks)

    suspend fun getChunksForDocument(documentId: String): List<MemoryChunk> =
        chunkDao.getChunksForDocument(documentId)

    fun observeChunksForDocument(documentId: String): Flow<List<MemoryChunk>> =
        chunkDao.observeChunksForDocument(documentId)

    suspend fun getChunkById(id: String): MemoryChunk? = chunkDao.getChunkById(id)

    suspend fun getAllChunks(): List<MemoryChunk> = chunkDao.getAllChunks()
}
