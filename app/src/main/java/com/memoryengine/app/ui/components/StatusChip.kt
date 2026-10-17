package com.memoryengine.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.memoryengine.app.ui.theme.Spacing

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    symbol: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Spacing.compact),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.compact, vertical = Spacing.base),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (symbol != null) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.width(Spacing.base))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}
