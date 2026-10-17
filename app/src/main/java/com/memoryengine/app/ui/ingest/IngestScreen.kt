package com.memoryengine.app.ui.ingest

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.UploadFile
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.AppTextField
import com.memoryengine.app.ui.components.ContextualHelpBottomSheet
import com.memoryengine.app.ui.components.ContextualHelpIcon
import com.memoryengine.app.ui.components.ErrorState
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.theme.Spacing

sealed interface IngestUiState {
    object Idle : IngestUiState
    data class Loading(val message: String = "Processing document & extracting memory chunks...") : IngestUiState
    data class Success(val title: String) : IngestUiState
    data class Error(val message: String) : IngestUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngestScreen(
    uiState: IngestUiState = IngestUiState.Idle,
    onFileSelected: (Uri) -> Unit = {},
    onTextSubmit: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var rawText by remember { mutableStateOf("") }
    var showHelpSheet by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onFileSelected(it) }
    }

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
            // Header Row with Title and ContextualHelpIcon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INGEST DOCUMENT",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "Import Source Material",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                ContextualHelpIcon(
                    contentDescription = "Supported File Formats Help",
                    onClick = { showHelpSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.section))

            when (uiState) {
                is IngestUiState.Loading -> {
                    IngestLoadingState(message = uiState.message)
                }
                else -> {
                    // StudioCard / AppCard for PDF/EPUB File Upload
                    AppCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.UploadFile,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(Spacing.iconSizeLarge)
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.standard))

                            Text(
                                text = "PDF or EPUB Document",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(Spacing.compact))

                            // Format badge visual indicator
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.compact),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusChip("PDF")
                                StatusChip("EPUB")
                                StatusChip("TXT")
                            }

                            Spacer(modifier = Modifier.height(Spacing.section))

                            PrimaryButton(
                                onClick = {
                                    filePickerLauncher.launch(
                                        arrayOf("application/pdf", "application/epub+zip", "text/plain")
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.UploadFile,
                                    contentDescription = null,
                                    modifier = Modifier.size(Spacing.iconSizeSmall)
                                )
                                Spacer(modifier = Modifier.width(Spacing.compact))
                                Text("Upload PDF / EPUB")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.major))

                    // Section Divider / Raw Text Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline)
                        )
                        Text(
                            text = " OR PASTE RAW TEXT ",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = Spacing.compact)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.section))

                    // AppCard for Raw Text Input
                    AppCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PASTE TEXT CONTENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${rawText.length} chars",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.compact))

                            AppTextField(
                                value = rawText,
                                onValueChange = { rawText = it },
                                placeholder = "Paste article text, book excerpts, or notes here...",
                                minLines = 5,
                                maxLines = 10,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(Spacing.standard))

                            PrimaryButton(
                                enabled = rawText.isNotBlank(),
                                onClick = {
                                    if (rawText.isNotBlank()) {
                                        onTextSubmit(rawText)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(Spacing.iconSizeSmall)
                                )
                                Spacer(modifier = Modifier.width(Spacing.compact))
                                Text("Ingest Raw Text")
                            }
                        }
                    }

                    if (uiState is IngestUiState.Success) {
                        Spacer(modifier = Modifier.height(Spacing.standard))
                        StatusBanner(
                            title = "Ingestion Complete",
                            message = "Successfully created '${uiState.title}' with memory chunks.",
                            isError = false
                        )
                    }

                    if (uiState is IngestUiState.Error) {
                        Spacer(modifier = Modifier.height(Spacing.standard))
                        ErrorState(
                            title = "Ingestion Failed",
                            message = uiState.message,
                            onRetry = {
                                if (rawText.isNotBlank()) {
                                    onTextSubmit(rawText)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showHelpSheet) {
        ContextualHelpBottomSheet(
            title = "Supported File Formats",
            concept = "PDF and EPUB documents, as well as direct raw text entry, are processed into structured memory chunks for active recall.",
            takeaway = "Upload clean digital ebooks or paste plain text for optimal chunking and prompt generation.",
            onDismissRequest = { showHelpSheet = false }
        )
    }
}

@Composable
fun StudioCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AppCard(modifier = modifier) {
        content()
    }
}

@Composable
fun PrimaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    PrimaryButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Spacing.iconSizeSmall)
        )
        Spacer(modifier = Modifier.width(Spacing.compact))
        Text(text = text)
    }
}

@Composable
private fun StatusBanner(
    title: String,
    message: String,
    isError: Boolean
) {
    val containerColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }
    val icon = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.CheckCircle

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = containerColor,
        borderColor = contentColor.copy(alpha = 0.3f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(Spacing.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(Spacing.standard))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun IngestLoadingState(
    message: String = "Processing document & extracting memory chunks...",
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceContainerLow,
        MaterialTheme.colorScheme.surfaceContainer,
        MaterialTheme.colorScheme.surfaceContainerLow
    )

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslation"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.section)
    ) {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(Spacing.standard))
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.standard)
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(brush)
                )
            }
        }
    }
}
