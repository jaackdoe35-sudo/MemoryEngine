package com.memoryengine.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memoryengine.app.ui.components.AnimatedExpandableCard
import com.memoryengine.app.ui.components.AnimatedProgressRing
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.RecallRatingBar
import com.memoryengine.app.ui.components.ShimmerLoadingState
import com.memoryengine.app.ui.components.pressScaleEffect
import com.memoryengine.app.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }
    var dailyRetentionProgress by remember { mutableFloatStateOf(0.85f) }
    var reviewGoalProgress by remember { mutableFloatStateOf(0.60f) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.screenPadding)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MEMORY ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "Workspace Overview",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                ContextualHelpIcon(
                    contentDescription = "Workspace Help",
                    onClick = { showHelpSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.section))

            // Action Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
            ) {
                Button(
                    onClick = {
                        isLoading = !isLoading
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .pressScaleEffect(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Autorenew,
                        contentDescription = "Simulate Loading",
                        modifier = Modifier.height(Spacing.iconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Spacing.compact))
                    Text(
                        text = if (isLoading) "Show Dashboard" else "Simulate Sync",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = {
                        dailyRetentionProgress = if (dailyRetentionProgress >= 0.95f) 0.50f else dailyRetentionProgress + 0.15f
                        reviewGoalProgress = if (reviewGoalProgress >= 0.95f) 0.40f else reviewGoalProgress + 0.20f
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .pressScaleEffect(),
                    border = BorderStroke(Spacing.cardBorderWidth, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "Update Progress",
                        modifier = Modifier.height(Spacing.iconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Spacing.compact))
                    Text(
                        text = "Boost Progress",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.section))

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ShimmerLoadingState()
            }

            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.section)) {
                    // Progress Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.standard)
                    ) {
                        AnimatedProgressRing(
                            progress = dailyRetentionProgress,
                            title = "Retention Rate",
                            modifier = Modifier.weight(1f),
                            helpTitle = "Memory Retention Rate",
                            helpConcept = "Calculates predicted memory retention across stored memory chunks using SM-2 recall history.",
                            helpTakeaway = "Maintain retention above 80% by completing daily recall prompts on schedule."
                        )

                        AnimatedProgressRing(
                            progress = reviewGoalProgress,
                            title = "Daily Goal",
                            modifier = Modifier.weight(1f),
                            helpTitle = "Daily Review Target",
                            helpConcept = "Measures daily active recall prompt completion relative to scheduled SM-2 intervals.",
                            helpTakeaway = "Completing daily prompts ensures long-term memory stability without cognitive overload."
                        )
                    }

                    // Interactive Rating Component Section
                    Text(
                        text = "ACTIVE RECALL SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    RecallRatingBar(
                        onRatingSelected = {}
                    )

                    // Expandable Memory Cards Section
                    Text(
                        text = "EXPANDABLE MEMORY CHUNKS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    AnimatedExpandableCard(
                        title = "Neural Plasticity & Synaptic Pruning",
                        subtitle = "4 prompts due for active review",
                        badgeText = "Memory Chunk #102",
                        initialExpanded = true,
                        helpTitle = "Memory Consolidation",
                        helpConcept = "Active retrieval triggers synaptic plasticity, converting short-term traces to long-term memory.",
                        helpTakeaway = "Engage in active recall before inspecting hints to maximize retention strength."
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
                            AppCard(
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ) {
                                Column {
                                    Text(
                                        text = "PROMPT QUEUE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.xxs))
                                    Text(
                                        text = "How does synaptic consolidation differ from systems consolidation?",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    AnimatedExpandableCard(
                        title = "SuperMemo SM-2 Interval Calculation",
                        subtitle = "Ease Factor: 2.50 | Next Interval: 6d",
                        badgeText = "Algorithm Core",
                        initialExpanded = false,
                        helpTitle = "SM-2 Interval Scheduling",
                        helpConcept = "Adjusts review intervals dynamically based on user quality ratings from 0 to 5.",
                        helpTakeaway = "Consistently rate prompts accurately to allow the scheduling engine to optimize intervals."
                    ) {
                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Text(
                                text = "Formula: I(n) = I(n-1) * EF, where EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showHelpSheet) {
        ContextualHelpBottomSheet(
            title = "Calm Editorial Workspace",
            concept = "The workspace minimizes clutter and maximizes focus through clean typography, visual hierarchy, and contextual help.",
            takeaway = "Tap any contextual help icon to understand the cognitive mechanism behind every feature.",
            onDismissRequest = { showHelpSheet = false }
        )
    }
}
