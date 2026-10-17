package com.memoryengine.app.ui.today

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.components.pressScaleEffect
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

            val isEmpty = state.totalPromptsCount == 0 && state.unfinishedDocumentsCount == 0

            if (isEmpty) {
                // Empty state: hide metrics per requirement "When there is no data, hide metrics and show a prominent call-to-action"
                EmptyState(
                    title = "Engine Ready",
                    message = "No facts or documents in your workspace yet. Import source material or add your first fact to begin active recall.",
                    icon = Icons.Outlined.AutoAwesome,
                    actionLabel = "Add your first fact",
                    onActionClick = onAddFact
                )
            } else {
                // Focus Score Card (only shown when there is user data)
                FocusScoreCard(
                    focusScore = state.focusScore,
                    onHelpClick = { showFocusScoreHelp = true }
                )

                // Review Session Card (if reviews are due)
                if (state.duePromptsCount > 0) {
                    ReviewSessionCard(
                        dueCount = state.duePromptsCount,
                        completedTodayCount = state.completedTodayCount,
                        onStartReview = onStartReview
                    )
                } else if (state.totalPromptsCount > 0) {
                    // All Caught Up Card
                    AllCaughtUpCard(totalCount = state.totalPromptsCount)
                }

                // Continue Ingesting Card
                if (state.unfinishedDocumentsCount > 0) {
                    ContinueIngestingCard(
                        pendingCount = state.unfinishedDocumentsCount,
                        onContinueIngest = onContinueIngest
                    )
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
        Column {
            Text(
                text = "Today",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Streak Badge using StatusChip styling
        StatusChip(
            text = "$streakDays DAY STREAK",
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun FocusScoreCard(
    focusScore: Int,
    onHelpClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = focusScore / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "focusScoreAnimation"
    )

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.base)
                ) {
                    Text(
                        text = "FOCUS SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ContextualHelpIcon(
                        onClick = onHelpClick,
                        size = 22.dp
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.base))

                Text(
                    text = "$focusScore%",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Spacing.iconSizeMedium)
                )
            }
        }
    }
}

@Composable
private fun ReviewSessionCard(
    dueCount: Int,
    completedTodayCount: Int,
    onStartReview: () -> Unit
) {
    val totalSession = dueCount + completedTodayCount
    val progressRatio = if (totalSession > 0) completedTodayCount.toFloat() / totalSession else 0f

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REVIEW SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(Spacing.base))

                    Text(
                        text = "$dueCount",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (dueCount == 1) "PROMPT DUE" else "PROMPTS DUE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Progress Ring Visual
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface,
                        strokeWidth = 7.dp,
                        strokeCap = StrokeCap.Round
                    )
                    CircularProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 7.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "$dueCount",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.section))

            PrimaryButton(
                onClick = onStartReview,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start Review Session")
                Spacer(modifier = Modifier.size(Spacing.compact))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(Spacing.iconSizeSmall)
                )
            }
        }
    }
}

@Composable
private fun AllCaughtUpCard(
    totalCount: Int
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.standard)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Spacing.iconSizeMedium)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ALL CAUGHT UP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Spacing.xxs))

                Text(
                    text = "$totalCount Active Memory Prompts",
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
        containerColor = MaterialTheme.colorScheme.surface,
        onClick = onContinueIngest
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.standard),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Spacing.iconSizeMedium)
                    )
                }

                Column {
                    Text(
                        text = "UNFINISHED DOCUMENTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Spacing.iconSizeSmall)
            )
        }
    }
}
