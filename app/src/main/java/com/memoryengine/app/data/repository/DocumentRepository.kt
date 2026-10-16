package com.memoryengine.app.data.repository

import com.memoryengine.app.data.local.dao.DocumentDao
import com.memoryengine.app.data.local.entity.Document
import kotlinx.coroutines.flow.Flow

class DocumentRepository(
    private val documentDao: DocumentDao
) {
    fun getAllDocuments(): Flow<List<Document>> = documentDao.getAllDocuments()

    suspend fun getDocumentById(id: String): Document? = documentDao.getDocumentById(id)

    suspend fun insertDocument(document: Document) = documentDao.insertDocument(document)

    suspend fun updateDocument(document: Document) = documentDao.updateDocument(document)

    suspend fun deleteDocument(id: String) = documentDao.deleteDocument(id)
}
