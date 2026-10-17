package com.memoryengine.app.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.AppTextField
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.theme.Spacing

@Composable
fun MemoryCreationScreen(
    viewModel: MemoryCreationViewModel,
    onMemorySaved: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MemoryCreationContent(
        uiState = uiState,
        onFactChange = viewModel::onFactChange,
        onAssociationChange = viewModel::onAssociationChange,
        onQuestionChange = viewModel::onQuestionChange,
        onSaveClick = {
            viewModel.saveMemory(onSuccess = onMemorySaved)
        },
        onHelpClick = viewModel::onShowHelp,
        onDismissHelpSheet = viewModel::onDismissHelp,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryCreationContent(
    uiState: MemoryCreationUiState,
    onFactChange: (String) -> Unit,
    onAssociationChange: (String) -> Unit,
    onQuestionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onHelpClick: () -> Unit,
    onDismissHelpSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.section)
        ) {
            // Header Row with Title and Contextual Help Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MANUAL CREATION",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Add Single Fact",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                ContextualHelpIcon(
                    onClick = onHelpClick,
                    contentDescription = "Help for manual memory creation"
                )
            }

            // Visual Hierarchy Card with Large Badge / Icon
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Spacing.iconSizeMedium)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.standard))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1 DISCRETE FACT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Direct Entry Unit",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    StatusChip(
                        text = "FAST TRACK",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
            }

            // Fact Field
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
                Text(
                    text = "Fact",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppTextField(
                    value = uiState.factText,
                    onValueChange = onFactChange,
                    placeholder = "e.g., Mitochondria is the powerhouse of the cell.",
                    isError = uiState.factError != null,
                    supportingText = uiState.factError
                        ?: "The core fact or knowledge unit to commit to long-term memory.",
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Location / Association Field
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
                Text(
                    text = "Location / Association",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppTextField(
                    value = uiState.associationText,
                    onValueChange = onAssociationChange,
                    placeholder = "e.g., Grandfather clock in the foyer (Memory Palace)",
                    supportingText = "Optional spatial cue. AI will generate an association if left blank.",
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Question Field
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
                Text(
                    text = "Question",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppTextField(
                    value = uiState.questionText,
                    onValueChange = onQuestionChange,
                    placeholder = "e.g., What cellular organelle generates energy?",
                    supportingText = "Active recall prompt question. AI generates one if left blank.",
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Success feedback card if saved
            if (uiState.isSaved) {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Saved",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Fact successfully created and scheduled!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.standard))

            // Primary Button
            PrimaryButton(
                enabled = !uiState.isLoading,
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(Spacing.iconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Save Memory",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // Contextual Help Bottom Sheet
        if (uiState.showHelpSheet) {
            ContextualHelpBottomSheet(
                title = "Manual Fact Creation",
                concept = "Adding discrete facts directly bypasses document chunking for instant active recall scheduling.",
                takeaway = "Provide a fact, an optional location or association cue, and a prompt question to convert knowledge into long-term memory.",
                onDismissRequest = onDismissHelpSheet
            )
        }
    }
}
