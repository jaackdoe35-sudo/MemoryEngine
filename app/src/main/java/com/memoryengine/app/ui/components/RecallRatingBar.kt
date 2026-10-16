package com.memoryengine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memoryengine.app.domain.scheduler.Rating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecallRatingBar(
    onRatingSelected: (Rating) -> Unit,
    modifier: Modifier = Modifier
) {
    var showHelpSheet by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RATE RECALL",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ContextualHelpIcon(
                    contentDescription = "Recall Help",
                    onClick = { showHelpSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RatingButton(
                    label = "Again",
                    intervalHint = "< 1d",
                    onClick = { onRatingSelected(Rating.AGAIN) },
                    modifier = Modifier.weight(1f)
                )
                RatingButton(
                    label = "Hard",
                    intervalHint = "1d",
                    onClick = { onRatingSelected(Rating.HARD) },
                    modifier = Modifier.weight(1f)
                )
                RatingButton(
                    label = "Good",
                    intervalHint = "6d",
                    onClick = { onRatingSelected(Rating.GOOD) },
                    modifier = Modifier.weight(1f)
                )
                RatingButton(
                    label = "Easy",
                    intervalHint = "12d",
                    onClick = { onRatingSelected(Rating.EASY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showHelpSheet) {
        ContextualHelpBottomSheet(
            title = "SM-2 Recall Ratings",
            concept = "Rating difficulty recalculates the optimal review interval using the SM-2 algorithm to maximize long-term memory retention.",
            takeaway = "Be honest with ratings: 'Again' resets failed items, while 'Easy' safely extends interval length.",
            onDismissRequest = { showHelpSheet = false }
        )
    }
}

@Composable
private fun RatingButton(
    label: String,
    intervalHint: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .pressScaleEffect(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = intervalHint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
