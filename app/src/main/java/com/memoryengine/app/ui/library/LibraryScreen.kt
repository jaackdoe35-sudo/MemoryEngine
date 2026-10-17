package com.memoryengine.app.ui.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.AppTextField
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.components.pressScaleEffect
import com.memoryengine.app.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onSelectDocument: (DocumentUiItem) -> Unit,
    onBackFromDrilldown: () -> Unit,
    onToggleHelpSheet: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    windowWidthSizeClass: WindowWidthSizeClass? = null
) {
    val configuration = LocalConfiguration.current
    val isTablet = windowWidthSizeClass == WindowWidthSizeClass.Medium ||
            windowWidthSizeClass == WindowWidthSizeClass.Expanded ||
            configuration.screenWidthDp >= 600

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isTablet) {
                // Two-Pane Tablet Layout
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Left Pane: Document List & Search
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                    ) {
                        DocumentListView(
                            documents = uiState.documents,
                            onSelectDocument = onSelectDocument,
                            onHelpClick = { onToggleHelpSheet(true) }
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline)
                    )

                    // Right Pane: Selected Document Detail / Drilldown
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                    ) {
                        if (uiState.selectedDocument != null) {
                            DocumentDrilldownView(
                                documentItem = uiState.selectedDocument,
                                chunks = uiState.selectedDocumentChunks,
                                onBackClick = onBackFromDrilldown,
                                onHelpClick = { onToggleHelpSheet(true) }
                            )
                        } else {
                            // Tablet Detail Empty State
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.standard))
                                    Text(
                                        text = "Select a document to inspect chunks & prompts",
                                        style = MaterialTheme.typography.titleMedium,
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
                        onBackClick = onBackFromDrilldown,
                        onHelpClick = { onToggleHelpSheet(true) }
                    )
                } else {
                    DocumentListView(
                        documents = uiState.documents,
                        onSelectDocument = onSelectDocument,
                        onHelpClick = { onToggleHelpSheet(true) }
                    )
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
}

@Composable
private fun DocumentListView(
    documents: List<DocumentUiItem>,
    onSelectDocument: (DocumentUiItem) -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredDocuments = documents.filter {
        it.document.title.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {
        Spacer(modifier = Modifier.height(Spacing.standard))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(Spacing.compact))
                StatusChip(
                    text = "${documents.size}",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }

            ContextualHelpIcon(onClick = onHelpClick)
        }

        Spacer(modifier = Modifier.height(Spacing.standard))

        // Search Bar for quick scanning and organization
        AppTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search library documents...",
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Spacing.standard))

        if (filteredDocuments.isEmpty()) {
            EmptyState(
                title = if (searchQuery.isBlank()) "No Documents Yet" else "No Matching Documents",
                message = if (searchQuery.isBlank())
                    "Upload documents to generate memory chunks and active recall prompts."
                else
                    "No documents match '$searchQuery'. Try adjusting your search term.",
                icon = Icons.Outlined.FolderOpen
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.standard)
            ) {
                items(
                    items = filteredDocuments,
                    key = { it.document.id }
                ) { item ->
                    DocumentStudioCard(
                        docItem = item,
                        onClick = { onSelectDocument(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentStudioCard(
    docItem: DocumentUiItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Spacing.iconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.standard))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = docItem.document.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.base))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatBadge(
                            icon = Icons.Outlined.Psychology,
                            label = "${docItem.chunkCount} CHUNKS"
                        )
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        StatBadge(
                            icon = Icons.Outlined.Quiz,
                            label = "${docItem.promptCount} PROMPTS"
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "View document details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Spacing.iconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.standard))

            // Visual Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MASTERY PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(docItem.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.base))
                LinearProgressIndicator(
                    progress = { docItem.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun StatBadge(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = Spacing.compact, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Spacing.iconSizeSmall)
        )
        Spacer(modifier = Modifier.width(Spacing.base))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    EmptyState(
        title = "No Documents Yet",
        message = "Upload documents to generate memory chunks and active recall prompts.",
        icon = Icons.Outlined.FolderOpen,
        modifier = modifier
    )
}

@Composable
fun DocumentDrilldownView(
    documentItem: DocumentUiItem,
    chunks: List<ChunkWithPrompts>,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {
        Spacer(modifier = Modifier.height(Spacing.standard))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .pressScaleEffect(onClick = onBackClick)
            ) {
                StatusChip(
                    text = "Back",
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = documentItem.document.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = Spacing.standard)
            )

            ContextualHelpIcon(onClick = onHelpClick)
        }

        Spacer(modifier = Modifier.height(Spacing.section))

        // Summary Card
        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${documentItem.chunkCount}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "CHUNKS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${documentItem.promptCount}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "PROMPTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(documentItem.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "MASTERY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.section))

        Text(
            text = "MEMORY CHUNKS & PROMPTS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Spacing.compact))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.standard)
        ) {
            items(
                items = chunks,
                key = { it.chunk.id }
            ) { chunkWithPrompts ->
                ChunkStudioCard(chunkWithPrompts = chunkWithPrompts)
            }
        }
    }
}

@Composable
fun ChunkStudioCard(
    chunkWithPrompts: ChunkWithPrompts,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(
                    text = "CHUNK #${chunkWithPrompts.chunk.orderIndex + 1}",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )

                Text(
                    text = "${chunkWithPrompts.chunk.wordCount} words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.compact))

            Text(
                text = chunkWithPrompts.chunk.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (chunkWithPrompts.prompts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.standard))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                Spacer(modifier = Modifier.height(Spacing.compact))

                Text(
                    text = "ASSOCIATED PROMPTS (${chunkWithPrompts.prompts.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.base))

                chunkWithPrompts.prompts.forEach { prompt ->
                    PromptItemCard(prompt = prompt)
                    Spacer(modifier = Modifier.height(Spacing.base))
                }
            }
        }
    }
}

@Composable
fun PromptItemCard(
    prompt: RecallPrompt,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Spacing.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Spacing.compact))
                Text(
                    text = prompt.question,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(Spacing.base))
            Text(
                text = "A: ${prompt.answer}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Spacing.iconSizeSmall + Spacing.compact)
            )
        }
    }
}
