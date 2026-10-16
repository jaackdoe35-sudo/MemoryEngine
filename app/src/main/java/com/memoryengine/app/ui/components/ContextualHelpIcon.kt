package com.memoryengine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.memoryengine.app.ui.theme.Spacing

@Composable
fun ContextualHelpIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Help information",
    size: Dp = Spacing.iconSizeLarge
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Spacing.cardBorderWidth,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .pressScaleEffect(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Spacing.iconSizeSmall)
        )
    }
}
