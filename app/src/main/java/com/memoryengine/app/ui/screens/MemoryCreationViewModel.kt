package com.memoryengine.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memoryengine.app.data.local.entity.Association
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.data.remote.service.AiService
import com.memoryengine.app.data.repository.AssociationRepository
import com.memoryengine.app.data.repository.DocumentRepository
import com.memoryengine.app.data.repository.MemoryChunkRepository
import com.memoryengine.app.data.repository.RecallPromptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MemoryCreationUiState(
    val factText: String = "",
    val associationText: String = "",
    val questionText: String = "",
    val factError: String? = null,
    val isLoading: Boolean = false,
    val showHelpSheet: Boolean = false,
    val isSaved: Boolean = false,
    val enhancedAssociation: String? = null
)

class MemoryCreationViewModel(
    private val documentRepository: DocumentRepository? = null,
    private val memoryChunkRepository: MemoryChunkRepository? = null,
    private val recallPromptRepository: RecallPromptRepository? = null,
    private val associationRepository: AssociationRepository? = null,
    private val aiService: AiService? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryCreationUiState())
    val uiState: StateFlow<MemoryCreationUiState> = _uiState.asStateFlow()

    fun onFactChange(text: String) {
        _uiState.update { it.copy(factText = text, factError = null) }
    }

    fun onAssociationChange(text: String) {
        _uiState.update { it.copy(associationText = text) }
    }

    fun onQuestionChange(text: String) {
        _uiState.update { it.copy(questionText = text) }
    }

    fun onShowHelp() {
        _uiState.update { it.copy(showHelpSheet = true) }
    }

    fun onDismissHelp() {
        _uiState.update { it.copy(showHelpSheet = false) }
    }

    fun saveMemory(onSuccess: (() -> Unit)? = null) {
        val currentState = _uiState.value
        if (currentState.factText.isBlank()) {
            _uiState.update { it.copy(factError = "Fact field cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, factError = null) }

            val fact = currentState.factText.trim()
            var association = currentState.associationText.trim()
            var question = currentState.questionText.trim()

            // Call AI service to enhance association if blank
            if (association.isBlank() && aiService != null) {
                try {
                    association = aiService.generateAssociation(fact)
                } catch (e: Exception) {
                    association = "Mnemonic link for: ${fact.take(30)}"
                }
            }

            // Call AI service to generate recall question if blank
            if (question.isBlank() && aiService != null) {
                try {
                    val promptData = aiService.generateRecallPrompt(fact)
                    question = promptData.question
                } catch (e: Exception) {
                    question = "What is the key fact for: ${fact.take(30)}?"
                }
            } else if (question.isBlank()) {
                question = "What is the key fact for: ${fact.take(30)}?"
            }

            val docId = UUID.randomUUID().toString()
            val chunkId = UUID.randomUUID().toString()
            val promptId = UUID.randomUUID().toString()

            val docTitle = if (fact.length > 30) fact.take(30) + "..." else fact
            val wordCount = fact.split("\\s+".toRegex()).size

            val document = Document(
                id = docId,
                title = docTitle,
                sourcePath = null,
                mimeType = "text/manual",
                chunkCount = 1
            )

            val chunk = MemoryChunk(
                id = chunkId,
                documentId = docId,
                content = fact,
                orderIndex = 0,
                wordCount = wordCount
            )

            val recallPrompt = RecallPrompt(
                id = promptId,
                chunkId = chunkId,
                question = question,
                answer = fact
            )

            documentRepository?.insertDocument(document)
            memoryChunkRepository?.insertChunk(chunk)
            recallPromptRepository?.insertRecallPrompt(recallPrompt)

            if (association.isNotBlank()) {
                val assocChunkId = UUID.randomUUID().toString()
                val assocChunk = MemoryChunk(
                    id = assocChunkId,
                    documentId = docId,
                    content = association,
                    orderIndex = 1,
                    wordCount = association.split("\\s+".toRegex()).size
                )
                memoryChunkRepository?.insertChunk(assocChunk)

                val associationEntity = Association(
                    id = UUID.randomUUID().toString(),
                    sourceChunkId = chunkId,
                    targetChunkId = assocChunkId,
                    relationshipType = "mnemonic"
                )
                associationRepository?.insertAssociation(associationEntity)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSaved = true,
                    associationText = association,
                    questionText = question,
                    enhancedAssociation = if (currentState.associationText.isBlank()) association else null
                )
            }

            onSuccess?.invoke()
        }
    }
}
