package com.memoryengine.app.data.repository

import com.memoryengine.app.data.local.dao.AssociationDao
import com.memoryengine.app.data.local.entity.Association
import kotlinx.coroutines.flow.Flow

class AssociationRepository(
    private val associationDao: AssociationDao
) {
    fun getAllAssociations(): Flow<List<Association>> = associationDao.getAllAssociations()

    suspend fun getAssociationsForChunk(chunkId: String): List<Association> =
        associationDao.getAssociationsForChunk(chunkId)

    suspend fun insertAssociation(association: Association) =
        associationDao.insertAssociation(association)

    suspend fun insertAssociations(associations: List<Association>) =
        associationDao.insertAssociations(associations)
}
