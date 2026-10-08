package br.com.ctech.bluequest.designsystem

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val MaterialColors = darkColorScheme(
    primary = BQColors.blue,
    onPrimary = BQColors.onBlue,
    primaryContainer = BQColors.blueDim,
    onPrimaryContainer = BQColors.blueBright,
    secondary = BQColors.blueBright,
    onSecondary = BQColors.onBlue,
    tertiary = BQColors.amber,
    onTertiary = BQColors.onAmber,
    background = BQColors.bg0,
    onBackground = BQColors.text1,
    surface = BQColors.bg1,
    onSurface = BQColors.text1,
    surfaceVariant = BQColors.bg2,
    onSurfaceVariant = BQColors.text2,
    surfaceContainerLowest = BQColors.bg0,
    surfaceContainerLow = BQColors.bg1,
    surfaceContainer = BQColors.bg1,
    surfaceContainerHigh = BQColors.bg2,
    surfaceContainerHighest = BQColors.bg2,
    outline = BQColors.stroke1,
    outlineVariant = BQColors.stroke1,
    error = BQColors.red,
    onError = BQColors.bg0,
    errorContainer = BQColors.redDim,
    onErrorContainer = BQColors.red,
)

@Composable
fun BlueQuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MaterialColors) {
        CompositionLocalProvider(
            LocalContentColor provides BQColors.text1,
            LocalTextStyle provides BQFont.body(BQTypeScale.body),
            content = content,
        )
    }
}