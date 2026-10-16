package com.memoryengine.app.ui.palace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Castle
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.pressScaleEffect
import com.memoryengine.app.ui.theme.Spacing

data class PalaceLocus(
    val id: String,
    val locusNumber: Int,
    val locationName: String,
    val locationIcon: ImageVector,
    val distinctiveAssociation: String,
    val originalContent: String
)

val samplePalaceLoci = listOf(
    PalaceLocus(
        id = "1",
        locusNumber = 1,
        locationName = "Front Entrance",
        locationIcon = Icons.Outlined.DoorFront,
        distinctiveAssociation = "Imagine a giant flaming apple glowing vividly on your front entrance mat.",
        originalContent = "The central concept begins with high energy input and immediate thermal activation."
    ),
    PalaceLocus(
        id = "2",
        locusNumber = 2,
        locationName = "Grand Hallway",
        locationIcon = Icons.Outlined.MeetingRoom,
        distinctiveAssociation = "A massive glowing compass floating mid-air, spinning golden light across the corridor.",
        originalContent = "Spatial orientation principles anchor memory pathways along physical routes."
    ),
    PalaceLocus(
        id = "3",
        locusNumber = 3,
        locationName = "Living Room Sofa",
        locationIcon = Icons.Outlined.Weekend,
        distinctiveAssociation = "A surreal neon tree growing through the cushions, sprouting crystal leaf nodes.",
        originalContent = "Decision trees evaluate branches dynamically using weight accumulation heuristics."
    ),
    PalaceLocus(
        id = "4",
        locusNumber = 4,
        locationName = "Kitchen Counter",
        locationIcon = Icons.Outlined.Restaurant,
        distinctiveAssociation = "An oversized hourglass pouring liquid silver onto marble, measuring precise time slices.",
        originalContent = "Temporal intervals determine spaced repetition scheduling and retention curves."
    ),
    PalaceLocus(
        id = "5",
        locusNumber = 5,
        locationName = "Master Bedroom",
        locationIcon = Icons.Outlined.Bed,
        distinctiveAssociation = "A starry galaxy constellation swirling above the bedframe, beaming soft memory threads.",
        originalContent = "Consolidation occurs during neural rest phases through rhythmic replay."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalaceScreen(
    modifier: Modifier = Modifier,
    title: String = "Memory Palace",
    loci: List<PalaceLocus> = samplePalaceLoci,
    onLocusClick: (PalaceLocus) -> Unit = {}
) {
    var showHelpBottomSheet by remember { mutableStateOf(false) }

    PalaceScreenContent(
        modifier = modifier,
        title = title,
        loci = loci,
        onLocusClick = onLocusClick,
        onHelpClick = { showHelpBottomSheet = true }
    )

    if (showHelpBottomSheet) {
        ContextualHelpBottomSheet(
            title = "Method of Loci",
            concept = "Spatial memory anchors abstract facts to familiar physical locations along a mental path.",
            takeaway = "Walk through your palace sequentially and visualize each distinctive association vividly at its assigned station.",
            onDismissRequest = { showHelpBottomSheet = false }
        )
    }
}

@Composable
fun PalaceScreenContent(
    modifier: Modifier = Modifier,
    title: String = "Memory Palace",
    loci: List<PalaceLocus> = samplePalaceLoci,
    onLocusClick: (PalaceLocus) -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    var selectedLocusId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar & Header Section
        PalaceHeader(
            title = title,
            totalLoci = loci.size,
            onHelpClick = onHelpClick
        )

        // Loci List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = Spacing.md,
                vertical = Spacing.sm
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(loci, key = { locus -> locus.id }) { locus ->
                PalaceLocusCard(
                    locus = locus,
                    isExpanded = selectedLocusId == locus.id,
                    onClick = {
                        selectedLocusId = if (selectedLocusId == locus.id) null else locus.id
                        onLocusClick(locus)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun PalaceHeader(
    title: String,
    totalLoci: Int,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            width = Spacing.cardBorderWidth,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Castle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Spacing.iconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.sm))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            ContextualHelpIcon(onClick = onHelpClick)
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Visual Progress & Stat Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = Spacing.cardBorderWidth,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(48.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "$totalLoci",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.md))

                    Column {
                        Text(
                            text = "ACTIVE SPATIAL ROUTE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$totalLoci Mnemonic Stations",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PalaceLocusCard(
    locus: PalaceLocus,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Spacing.cardBorderWidth,
                color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .pressScaleEffect(onClick = onClick)
            .padding(Spacing.md)
    ) {
        Column {
            // Station Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Locus Number Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(
                                width = Spacing.cardBorderWidth,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${locus.locusNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.sm))

                    // Location Icon Box
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = Spacing.cardBorderWidth,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = locus.locationIcon,
                            contentDescription = locus.locationName,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(Spacing.iconSizeMedium)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.sm))

                    Text(
                        text = locus.locationName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // AI-Generated Distinctive Visual Association Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .border(
                        width = Spacing.cardBorderWidth,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(Spacing.md)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "AI Distinctive Association",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(Spacing.iconSizeMedium)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(Spacing.sm))

                    Column {
                        Text(
                            text = "DISTINCTIVE ASSOCIATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = locus.distinctiveAssociation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Original content chunk snippet shown when expanded or available
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(
                                width = Spacing.cardBorderWidth,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(Spacing.sm)
                    ) {
                        Column {
                            Text(
                                text = "ORIGINAL MEMORY CHUNK",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = locus.originalContent,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
