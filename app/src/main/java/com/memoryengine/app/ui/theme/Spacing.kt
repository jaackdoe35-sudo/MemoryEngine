package com.memoryengine.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Spacing {
    // Design Spec Tokens
    val base: Dp = 4.dp
    val compact: Dp = 8.dp
    val standard: Dp = 16.dp
    val section: Dp = 24.dp
    val major: Dp = 32.dp

    val screenPadding: Dp = 20.dp
    val cardRadius: Dp = 18.dp
    val textFieldRadius: Dp = 12.dp
    val buttonHeight: Dp = 52.dp
    val minTouchTarget: Dp = 48.dp

    // Aliases / Compatibility Tokens
    val xxs: Dp = 2.dp
    val xs: Dp = base          // 4.dp
    val sm: Dp = compact       // 8.dp
    val md: Dp = standard      // 16.dp
    val lg: Dp = section       // 24.dp
    val xl: Dp = major         // 32.dp
    val xxl: Dp = 48.dp

    val cardBorderWidth: Dp = 1.dp
    val iconSizeSmall: Dp = 20.dp
    val iconSizeMedium: Dp = 24.dp
    val iconSizeLarge: Dp = 32.dp
}
