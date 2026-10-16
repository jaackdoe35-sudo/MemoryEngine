package com.memoryengine.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memoryengine.app.data.local.entity.MemoryChunk
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryChunkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunk(chunk: MemoryChunk)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<MemoryChunk>)

    @Query("SELECT * FROM memory_chunks WHERE documentId = :documentId ORDER BY orderIndex ASC")
    suspend fun getChunksForDocument(documentId: String): List<MemoryChunk>

    @Query("SELECT * FROM memory_chunks WHERE documentId = :documentId ORDER BY orderIndex ASC")
    fun observeChunksForDocument(documentId: String): Flow<List<MemoryChunk>>

    @Query("SELECT * FROM memory_chunks WHERE id = :id")
    suspend fun getChunkById(id: String): MemoryChunk?

    @Query("SELECT * FROM memory_chunks")
    suspend fun getAllChunks(): List<MemoryChunk>

    @Query("DELETE FROM memory_chunks WHERE documentId = :documentId")
    suspend fun deleteChunksForDocument(documentId: String)
}
