package com.memoryengine.app.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memoryengine.app.data.repository.DocumentRepository
import com.memoryengine.app.data.repository.RecallPromptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TodayViewModel(
    private val documentRepository: DocumentRepository,
    private val promptRepository: RecallPromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState(isLoading = true))
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init {
        loadTodayDashboard()
    }

    fun loadTodayDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                documentRepository.getAllDocuments(),
                promptRepository.getAllPrompts()
            ) { documents, prompts ->
                val now = System.currentTimeMillis()
                val duePrompts = prompts.filter { it.nextReviewAt <= now }
                val totalPrompts = prompts.size
                val completedToday = prompts.count { prompt -> prompt.reviewCount > 0 }
                val unfinishedDocs = documents.count { doc -> doc.title.isNotEmpty() }

                val focusScore = if (totalPrompts > 0) {
                    val reviewedRatio = (totalPrompts - duePrompts.size).toFloat() / totalPrompts
                    (reviewedRatio * 100).toInt().coerceIn(50, 100)
                } else {
                    0
                }

                TodayUiState(
                    duePromptsCount = duePrompts.size,
                    totalPromptsCount = totalPrompts,
                    completedTodayCount = completedToday,
                    unfinishedDocumentsCount = unfinishedDocs,
                    focusScore = focusScore,
                    streakDays = if (completedToday > 0) 1 else 0,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
