package com.memoryengine.app.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.theme.Spacing

data class TodayUiState(
    val duePromptsCount: Int = 0,
    val totalPromptsCount: Int = 0,
    val completedTodayCount: Int = 0,
    val unfinishedDocumentsCount: Int = 0,
    val focusScore: Int = 85,
    val streakDays: Int = 7,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    state: TodayUiState,
    onStartReview: () -> Unit = {},
    onContinueIngest: () -> Unit = {},
    onAddFact: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showFocusScoreHelp by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.standard)
        ) {
            // Header: Title & Streak Badge
            TodayHeader(streakDays = state.streakDays)

            // Focus Score Card
            FocusScoreCard(
                focusScore = state.focusScore,
                onHelpClick = { showFocusScoreHelp = true }
            )

            // Conditional Dashboard Content based on State
            when {
                // Empty state (New User / No database items)
                state.totalPromptsCount == 0 && state.unfinishedDocumentsCount == 0 -> {
                    EmptyState(
                        title = "Engine Ready",
                        message = "No facts or documents in database. Ingest memory items or create a new prompt to get started.",
                        actionLabel = "Add First Fact",
                        onActionClick = onAddFact
                    )
                }

                else -> {
                    // Review Session Card (if reviews are due)
                    if (state.duePromptsCount > 0) {
                        ReviewSessionCard(
                            dueCount = state.duePromptsCount,
                            completedTodayCount = state.completedTodayCount,
                            onStartReview = onStartReview
                        )
                    } else if (state.totalPromptsCount > 0) {
                        // All Caught Up Card (if no reviews due but total prompts > 0)
                        AllCaughtUpCard(totalCount = state.totalPromptsCount)
                    }

                    // Continue Ingesting Card (if unfinished documents exist)
                    if (state.unfinishedDocumentsCount > 0) {
                        ContinueIngestingCard(
                            pendingCount = state.unfinishedDocumentsCount,
                            onContinueIngest = onContinueIngest
                        )
                    }
                }
            }
        }
    }

    if (showFocusScoreHelp) {
        ContextualHelpBottomSheet(
            title = "Focus Score & Spaced Repetition",
            concept = "Active recall scheduled right before forgetting optimizes long-term memory retention.",
            takeaway = "Review items daily when prompted to keep your Focus Score high and minimize future study time.",
            onDismissRequest = { showFocusScoreHelp = false }
        )
    }
}

@Composable
private fun TodayHeader(streakDays: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Today",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        StatusChip(
            text = "$streakDays DAY STREAK",
            symbol = "🔥",
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun FocusScoreCard(
    focusScore: Int,
    onHelpClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.standard),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    Text(
                        text = "FOCUS SCORE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ContextualHelpIcon(
                        onClick = onHelpClick,
                        size = 20.dp
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.compact))

                Text(
                    text = "$focusScore%",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Icon(
                imageVector = Icons.Outlined.Psychology,
                contentDescription = "Focus Score Icon",
                modifier = Modifier.padding(Spacing.compact),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ReviewSessionCard(
    dueCount: Int,
    completedTodayCount: Int,
    onStartReview: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(Spacing.standard)) {
            Text(
                text = "REVIEW QUEUE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(Spacing.compact))

            Text(
                text = "$dueCount Items Ready for Review",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (completedTodayCount > 0) {
                Spacer(modifier = Modifier.height(Spacing.base))
                Text(
                    text = "$completedTodayCount completed today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.standard))

            PrimaryButton(
                text = "Start Review Queue",
                onClick = onStartReview,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun AllCaughtUpCard(
    totalCount: Int
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(Spacing.standard),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.standard)
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ALL CAUGHT UP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(Spacing.base))

                Text(
                    text = "$totalCount Memory Items Active",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ContinueIngestingCard(
    pendingCount: Int,
    onContinueIngest: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onContinueIngest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.standard),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.standard),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Column {
                    Text(
                        text = "UNFINISHED DOCUMENTS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.base))
                    Text(
                        text = "$pendingCount Pending Documents",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Resume Ingestion",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
