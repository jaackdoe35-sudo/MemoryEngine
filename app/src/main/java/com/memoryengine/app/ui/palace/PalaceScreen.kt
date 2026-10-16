package com.memoryengine.app.ui.palace

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

data class PalaceItem(
    val id: String,
    val locusIndex: Int,
    val locationName: String,
    val locationIcon: ImageVector,
    val chunkTitle: String,
    val distinctiveAssociation: String
)

val SamplePalaceItems = listOf(
    PalaceItem(
        id = "1",
        locusIndex = 1,
        locationName = "Kitchen Table",
        locationIcon = Icons.Outlined.Restaurant,
        chunkTitle = "Cellular Respiration - Glycolysis",
        distinctiveAssociation = "Imagine a giant flaming apple on your kitchen table, splitting violently into two glowing pyruvates."
    ),
    PalaceItem(
        id = "2",
        locusIndex = 2,
        locationName = "Front Door",
        locationIcon = Icons.Outlined.DoorFront,
        chunkTitle = "Krebs Cycle Initiation",
        distinctiveAssociation = "Visualize a giant spinning brass citric acid key unlocking the front door, spewing carbon dioxide sparks."
    ),
    PalaceItem(
        id = "3",
        locusIndex = 3,
        locationName = "Living Room Sofa",
        locationIcon = Icons.Outlined.Weekend,
        chunkTitle = "Electron Transport Chain",
        distinctiveAssociation = "Picture electric blue lightning bolts bouncing along the sofa cushions while powering high-voltage ATP synthesizers."
    ),
    PalaceItem(
        id = "4",
        locusIndex = 4,
        locationName = "Study Bookshelf",
        locationIcon = Icons.AutoMirrored.Outlined.MenuBook,
        chunkTitle = "Oxidative Phosphorylation",
        distinctiveAssociation = "See waterfall streams of shimmering water cascading down book spines, creating a glowing cloud of energy."
    ),
    PalaceItem(
        id = "5",
        locusIndex = 5,
        locationName = "Bedroom Bed",
        locationIcon = Icons.Outlined.Bed,
        chunkTitle = "Fermentation Route",
        distinctiveAssociation = "A massive floating bubble of yeast rests on the mattress, rising and expanding in rhythm like a sleeping giant."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalaceScreen(
    items: List<PalaceItem> = SamplePalaceItems,
    palaceName: String = "Biological Energetics Palace",
    onItemClick: ((PalaceItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showHelpBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Memory Palace",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    ContextualHelpIcon(
                        onClick = { showHelpBottomSheet = true },
                        modifier = Modifier.padding(end = Spacing.md)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header visual summary with visual metrics (numbers, progress ring)
            PalaceHeaderCard(
                palaceName = palaceName,
                totalLoci = items.size,
                completedLoci = items.size
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Loci items list
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = Spacing.md,
                    vertical = Spacing.sm
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items, key = { it.id }) { item ->
                    PalaceLocusCard(
                        item = item,
                        onClick = { onItemClick?.invoke(item) }
                    )
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
private fun PalaceHeaderCard(
    palaceName: String,
    totalLoci: Int,
    completedLoci: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalLoci > 0) completedLoci.toFloat() / totalLoci.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md)
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Spacing.iconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "SPATIAL ROUTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = palaceName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            // Progress Ring & Visual Metric
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$completedLoci/$totalLoci",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "LOCI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PalaceLocusCard(
    item: PalaceItem,
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
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .pressScaleEffect(onClick = onClick)
            .padding(Spacing.md)
    ) {
        Column {
            // Locus step badge & location tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Locus Index Number Badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${item.locusIndex}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.sm))

                    // Location Icon & Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                    ) {
                        Icon(
                            imageVector = item.locationIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Spacing.iconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = item.locationName,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = item.chunkTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Distinctive Association Box (Visually distinct card layout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = Spacing.cardBorderWidth,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(Spacing.md)
            ) {
                Column {
                    Text(
                        text = "DISTINCTIVE ASSOCIATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = item.distinctiveAssociation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
