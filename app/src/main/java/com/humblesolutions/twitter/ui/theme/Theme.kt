package com.humblesolutions.twitter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    background    = TwitterBlack,
    surface       = TwitterDarkSurface,
    primary       = TwitterBlue,
    onBackground  = Color.White,
    onSurface     = Color.White,
    onPrimary     = Color.White,
)

@Composable
fun TwitterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography  = Typography,
        content     = content
    )
}