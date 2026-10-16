package com.memoryengine.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memoryengine.app.data.local.entity.Association
import kotlinx.coroutines.flow.Flow

@Dao
interface AssociationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociation(association: Association)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociations(associations: List<Association>)

    @Query("SELECT * FROM associations WHERE sourceChunkId = :chunkId OR targetChunkId = :chunkId")
    suspend fun getAssociationsForChunk(chunkId: String): List<Association>

    @Query("SELECT * FROM associations")
    fun getAllAssociations(): Flow<List<Association>>
}
