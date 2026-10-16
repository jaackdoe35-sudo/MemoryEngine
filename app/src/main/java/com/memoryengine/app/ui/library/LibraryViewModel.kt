package com.memoryengine.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.data.repository.DocumentRepository
import com.memoryengine.app.data.repository.MemoryChunkRepository
import com.memoryengine.app.data.repository.RecallPromptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ChunkWithPrompts(
    val chunk: MemoryChunk,
    val prompts: List<RecallPrompt>
)

data class DocumentUiItem(
    val document: Document,
    val chunkCount: Int,
    val promptCount: Int,
    val progress: Float
)

data class LibraryUiState(
    val documents: List<DocumentUiItem> = emptyList(),
    val selectedDocument: DocumentUiItem? = null,
    val selectedDocumentChunks: List<ChunkWithPrompts> = emptyList(),
    val isLoading: Boolean = false,
    val showHelpSheet: Boolean = false,
    val errorMessage: String? = null,
    val onRetry: (() -> Unit)? = null
)

class LibraryViewModel(
    private val documentRepository: DocumentRepository,
    private val chunkRepository: MemoryChunkRepository,
    private val promptRepository: RecallPromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                documentRepository.getAllDocuments(),
                promptRepository.getAllPrompts()
            ) { documents, allPrompts ->
                val uiItems = documents.map { doc ->
                    val chunks = chunkRepository.getChunksForDocument(doc.id)
                    val chunkIds = chunks.map { it.id }.toSet()
                    val docPrompts = allPrompts.filter { it.chunkId in chunkIds }
                    val reviewedPrompts = docPrompts.count { it.reviewCount > 0 }
                    val progress = if (docPrompts.isNotEmpty()) {
                        reviewedPrompts.toFloat() / docPrompts.size
                    } else if (chunks.isNotEmpty()) {
                        1f
                    } else {
                        0f
                    }
                    DocumentUiItem(
                        document = doc,
                        chunkCount = chunks.size,
                        promptCount = docPrompts.size,
                        progress = progress
                    )
                }
                uiItems
            }.collect { docItems ->
                _uiState.value = _uiState.value.copy(
                    documents = docItems,
                    isLoading = false
                )
            }
        }
    }

    fun selectDocument(documentItem: DocumentUiItem) {
        viewModelScope.launch {
            val chunks = chunkRepository.getChunksForDocument(documentItem.document.id)
            val chunksWithPrompts = chunks.map { chunk ->
                val prompts = promptRepository.getPromptsForChunk(chunk.id)
                ChunkWithPrompts(chunk = chunk, prompts = prompts)
            }
            _uiState.value = _uiState.value.copy(
                selectedDocument = documentItem,
                selectedDocumentChunks = chunksWithPrompts
            )
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedDocument = null,
            selectedDocumentChunks = emptyList()
        )
    }

    fun toggleHelpSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHelpSheet = show)
    }
}
