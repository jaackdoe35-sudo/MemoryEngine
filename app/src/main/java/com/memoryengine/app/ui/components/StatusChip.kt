package com.memoryengine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.memoryengine.app.ui.theme.Spacing

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .border(
                width = Spacing.cardBorderWidth,
                color = borderColor,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = Spacing.compact, vertical = Spacing.base)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}
