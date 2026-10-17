package com.memoryengine.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memoryengine.app.domain.scheduler.Rating
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.RecallRatingBar
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.theme.Spacing

data class RecallUiState(
    val currentCardIndex: Int = 1,
    val totalCards: Int = 10,
    val question: String = "",
    val answer: String = "",
    val association: String? = null,
    val isAnswerRevealed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecallScreen(
    uiState: RecallUiState,
    onRevealAnswer: () -> Unit,
    onRatingSelected: (Rating) -> Unit,
    onBackToToday: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showHelpSheet by remember { mutableStateOf(false) }

    if (uiState.totalCards == 0) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.screenPadding),
            contentAlignment = Alignment.Center
        ) {
            EmptyState(
                title = "No Due Prompts",
                message = "You are all caught up! No active recall prompts are due right now.",
                actionLabel = "Return Today",
                onActionClick = { onBackToToday?.invoke() }
            )
        }
        return
    }

    val progress = if (uiState.totalCards > 0) {
        uiState.currentCardIndex.toFloat() / uiState.totalCards.toFloat()
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.screenPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Linear progress indicator & Card counter header
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )

                Spacer(modifier = Modifier.height(Spacing.standard))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(
                        text = "${uiState.currentCardIndex} / ${uiState.totalCards}",
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.primary
                    )

                    ContextualHelpIcon(
                        contentDescription = "Active Recall Help",
                        onClick = { showHelpSheet = true }
                    )
                }
            }

            // Center Section: Question, Answer & Association
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "QUESTION",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    Text(
                        text = uiState.question,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = Spacing.compact)
                    )

                    AnimatedVisibility(
                        visible = uiState.isAnswerRevealed,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.major),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Answer Card
                            AppCard(
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(Spacing.standard)) {
                                    Text(
                                        text = "ANSWER",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.base))
                                    Text(
                                        text = uiState.answer,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Association Card
                            if (!uiState.association.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(Spacing.standard))

                                AppCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                ) {
                                    Column(modifier = Modifier.padding(Spacing.standard)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.AutoAwesome,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(Spacing.compact))
                                            Text(
                                                text = "DISTINCTIVE ASSOCIATION",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(Spacing.base))
                                        Text(
                                            text = uiState.association,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Section: Controls
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!uiState.isAnswerRevealed) {
                    PrimaryButton(
                        text = "Reveal Answer",
                        onClick = onRevealAnswer
                    )
                } else {
                    RecallRatingBar(
                        onRatingSelected = onRatingSelected
                    )
                }
            }
        }

        if (showHelpSheet) {
            ContextualHelpBottomSheet(
                title = "Active Recall Practice",
                concept = "Active recall forces the brain to retrieve information from memory rather than passively re-reading it, building strong neural pathways.",
                takeaway = "Attempt to state the full answer out loud or in your mind before tapping 'Reveal Answer'.",
                onDismissRequest = { showHelpSheet = false }
            )
        }
    }
}
