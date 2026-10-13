package com.example.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Klasifikasi ukuran layar standar Material 3 WindowSizeClass
 * untuk mendukung ponsel, foldable, dan tablet.
 */
enum class WindowWidthSize {
    COMPACT, // Lebar < 600.dp (Smartphone Portrait)
    MEDIUM,  // 600.dp <= Lebar < 840.dp (Foldable / Tablet Portrait)
    EXPANDED // Lebar >= 840.dp (Tablet Landscape / Desktop)
}

data class AdaptiveWindowInfo(
    val widthSizeClass: WindowWidthSize,
    val maxWidthDp: Dp,
    val maxHeightDp: Dp
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ResponsiveLayoutContainer(
    modifier: Modifier = Modifier,
    compactContent: @Composable (AdaptiveWindowInfo) -> Unit,
    mediumContent: (@Composable (AdaptiveWindowInfo) -> Unit)? = null,
    expandedContent: (@Composable (AdaptiveWindowInfo) -> Unit)? = null
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthSize = when {
            maxWidth < 600.dp -> WindowWidthSize.COMPACT
            maxWidth < 840.dp -> WindowWidthSize.MEDIUM
            else -> WindowWidthSize.EXPANDED
        }

        val windowInfo = AdaptiveWindowInfo(
            widthSizeClass = widthSize,
            maxWidthDp = maxWidth,
            maxHeightDp = maxHeight
        )

        when (widthSize) {
            WindowWidthSize.COMPACT -> compactContent(windowInfo)
            WindowWidthSize.MEDIUM -> (mediumContent ?: compactContent).invoke(windowInfo)
            WindowWidthSize.EXPANDED -> (expandedContent ?: mediumContent ?: compactContent).invoke(windowInfo)
        }
    }
}
