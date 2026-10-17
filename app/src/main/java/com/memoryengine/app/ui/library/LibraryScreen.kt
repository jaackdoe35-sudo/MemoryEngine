package com.memoryengine.app.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.AppTextField
import com.memoryengine.app.ui.components.AppTopBar
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onSelectDocument: (DocumentUiItem) -> Unit,
    onBackFromDrilldown: () -> Unit,
    onToggleHelpSheet: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    windowWidthSizeClass: WindowWidthSizeClass? = null
) {
    val configuration = LocalConfiguration.current
    val isTablet = windowWidthSizeClass == WindowWidthSizeClass.Medium ||
            windowWidthSizeClass == WindowWidthSizeClass.Expanded ||
            configuration.screenWidthDp >= 600

    var searchQuery by remember { mutableStateOf("") }

    val filteredDocuments = uiState.documents.filter {
        it.document.title.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Library",
                actions = {
                    ContextualHelpIcon(
                        onClick = { onToggleHelpSheet(true) },
                        modifier = Modifier.padding(end = Spacing.standard)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = Spacing.screenPadding, vertical = Spacing.compact)) {
                AppTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchQueryChange(it)
                    },
                    placeholder = "Search library documents...",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.compact))

            if (isTablet) {
                // Two-Pane Tablet Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                    ) {
                        DocumentListView(
                            documents = filteredDocuments,
                            onSelectDocument = onSelectDocument
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .padding(vertical = Spacing.standard)
                    )

                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                    ) {
                        if (uiState.selectedDocument != null) {
                            DocumentDrilldownView(
                                documentItem = uiState.selectedDocument,
                                chunks = uiState.selectedDocumentChunks,
                                onBackClick = onBackFromDrilldown
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.standard))
                                    Text(
                                        text = "Select a document to inspect chunks & prompts",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Single Pane Mobile Layout
                if (uiState.selectedDocument != null) {
                    DocumentDrilldownView(
                        documentItem = uiState.selectedDocument,
                        chunks = uiState.selectedDocumentChunks,
                        onBackClick = onBackFromDrilldown
                    )
                } else {
                    DocumentListView(
                        documents = filteredDocuments,
                        onSelectDocument = onSelectDocument
                    )
                }
            }
        }

        if (uiState.showHelpSheet) {
            ContextualHelpBottomSheet(
                title = "Knowledge Library",
                concept = "Documents are transformed into memory chunks and targeted recall prompts.",
                takeaway = "Review document progress and memory chunks to track your retention over time.",
                onDismissRequest = { onToggleHelpSheet(false) }
            )
        }
    }
}

@Composable
private fun DocumentListView(
    documents: List<DocumentUiItem>,
    onSelectDocument: (DocumentUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (documents.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Spacing.screenPadding),
            contentAlignment = Alignment.Center
        ) {
            EmptyState(
                title = "No Documents Found",
                message = "Upload documents to generate memory chunks and active recall prompts.",
                icon = Icons.Outlined.FolderOpen
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.standard)
        ) {
            items(
                items = documents,
                key = { it.document.id }
            ) { item ->
                DocumentCardItem(
                    docItem = item,
                    onClick = { onSelectDocument(item) }
                )
            }
        }
    }
}

@Composable
fun DocumentCardItem(
    docItem: DocumentUiItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(Spacing.standard)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = docItem.document.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusChip(
                    text = "${(docItem.progress * 100).toInt()}%",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(Spacing.compact))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.standard),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Spacing.base))
                    Text(
                        text = "${docItem.chunkCount} Chunks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Quiz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Spacing.base))
                    Text(
                        text = "${docItem.promptCount} Prompts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.compact))

            LinearProgressIndicator(
                progress = { docItem.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }
    }
}

@Composable
fun DocumentDrilldownView(
    documentItem: DocumentUiItem,
    chunks: List<ChunkWithPrompts>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AppCard(
                onClick = onBackClick,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(modifier = Modifier.padding(Spacing.compact)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.standard))

            Text(
                text = documentItem.document.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(Spacing.standard))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.standard)
        ) {
            items(
                items = chunks,
                key = { it.chunk.id }
            ) { chunkWithPrompts ->
                ChunkCardItem(chunkWithPrompts = chunkWithPrompts)
            }
        }
    }
}

@Composable
fun ChunkCardItem(
    chunkWithPrompts: ChunkWithPrompts,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(Spacing.standard)) {
            StatusChip(
                text = "CHUNK #${chunkWithPrompts.chunk.orderIndex + 1}",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(Spacing.compact))

            Text(
                text = chunkWithPrompts.chunk.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (chunkWithPrompts.prompts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.standard))
                Text(
                    text = "ASSOCIATED PROMPTS (${chunkWithPrompts.prompts.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(Spacing.compact))

                chunkWithPrompts.prompts.forEach { prompt ->
                    PromptListItem(prompt = prompt)
                    Spacer(modifier = Modifier.height(Spacing.base))
                }
            }
        }
    }
}

@Composable
fun PromptListItem(
    prompt: RecallPrompt,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(modifier = Modifier.padding(Spacing.compact)) {
            Text(
                text = "Q: ${prompt.question}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Spacing.base))
            Text(
                text = "A: ${prompt.answer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
