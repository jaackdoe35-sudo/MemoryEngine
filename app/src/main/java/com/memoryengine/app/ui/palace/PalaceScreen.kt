package com.memoryengine.app.ui.palace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.memoryengine.app.data.local.entity.Association
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.AppTopBar
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.theme.Spacing

data class MemoryPalaceUiState(
    val palaceName: String = "Main Palace",
    val roomName: String = "Central Hall",
    val chunks: List<MemoryChunk> = emptyList(),
    val associations: List<Association> = emptyList(),
    val selectedChunkId: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalaceScreen(
    state: MemoryPalaceUiState = MemoryPalaceUiState(),
    onSelectChunk: (String) -> Unit = {},
    onCreateAssociation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showHelpBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Memory Palace",
                actions = {
                    ContextualHelpIcon(
                        onClick = { showHelpBottomSheet = true },
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
            // Spatial Grouping Header: Palace & Room structure
            SpatialGroupingHeader(
                palaceName = state.palaceName,
                roomName = state.roomName,
                itemCount = state.chunks.size
            )

            Spacer(modifier = Modifier.height(Spacing.standard))

            if (state.chunks.isEmpty()) {
                Box(modifier = Modifier.padding(Spacing.screenPadding)) {
                    EmptyState(
                        title = "Empty Palace Room",
                        message = "No memory loci defined in this room. Add chunks or create associations to place items in your palace.",
                        actionLabel = "Create Association",
                        onActionClick = onCreateAssociation
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = Spacing.screenPadding,
                        vertical = Spacing.compact
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.standard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(state.chunks, key = { _, chunk -> chunk.id }) { index, chunk ->
                        val isSelected = chunk.id == state.selectedChunkId
                        val chunkAssociations = state.associations.filter {
                            it.sourceChunkId == chunk.id || it.targetChunkId == chunk.id
                        }

                        PalaceLocusCard(
                            locusIndex = index + 1,
                            chunk = chunk,
                            associations = chunkAssociations,
                            isSelected = isSelected,
                            onClick = { onSelectChunk(chunk.id) }
                        )
                    }
                }
            }
        }

        if (showHelpBottomSheet) {
            ContextualHelpBottomSheet(
                title = "Method of Loci (Memory Palace)",
                concept = "Spatial memory anchors abstract facts to physical places in a familiar room or route.",
                takeaway = "Walk through each locus mentally and visualize the vivid, absurd association to trigger instant recall.",
                onDismissRequest = { showHelpBottomSheet = false }
            )
        }
    }
}

@Composable
private fun SpatialGroupingHeader(
    palaceName: String,
    roomName: String,
    itemCount: Int
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(Spacing.standard)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
            ) {
                Icon(
                    imageVector = Icons.Default.HomeWork,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = palaceName.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.compact))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = roomName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                StatusChip(
                    text = "$itemCount LOCI",
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PalaceLocusCard(
    locusIndex: Int,
    chunk: MemoryChunk,
    associations: List<Association>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = containerColor,
        borderColor = borderColor,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(Spacing.standard)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Locus #$locusIndex",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isSelected) {
                    StatusChip(
                        text = "ACTIVE",
                        symbol = "✓",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.compact))

            Text(
                text = chunk.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (associations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.standard))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(Spacing.compact)
                ) {
                    Text(
                        text = "DISTINCTIVE ASSOCIATIONS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    associations.forEach { assoc ->
                        Spacer(modifier = Modifier.height(Spacing.base))
                        Text(
                            text = "• ${assoc.relationshipType.ifEmpty { "Association link" }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
