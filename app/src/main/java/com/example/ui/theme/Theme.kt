package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = OnEmeraldPrimaryDark,
    primaryContainer = EmeraldPrimaryContainerDark,
    onPrimaryContainer = OnEmeraldPrimaryContainerDark,
    secondary = AmberSecondaryDark,
    onSecondary = OnAmberSecondaryDark,
    secondaryContainer = AmberSecondaryContainerDark,
    onSecondaryContainer = OnAmberSecondaryContainerDark,
    tertiary = CobaltTertiaryDark,
    onTertiary = OnCobaltTertiaryDark,
    tertiaryContainer = CobaltTertiaryContainerDark,
    onTertiaryContainer = OnCobaltTertiaryContainerDark,
    background = LedgerBackgroundDark,
    onBackground = OnLedgerBackgroundDark,
    surface = LedgerSurfaceDark,
    onSurface = OnLedgerSurfaceDark,
    surfaceVariant = LedgerSurfaceVariantDark,
    onSurfaceVariant = OnLedgerSurfaceVariantDark,
    outline = LedgerOutlineDark,
    outlineVariant = LedgerOutlineVariantDark,
    error = LedgerErrorDark,
    onError = OnLedgerErrorDark,
    errorContainer = LedgerErrorContainerDark,
    onErrorContainer = OnLedgerErrorContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = OnEmeraldPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = OnEmeraldPrimaryContainer,
    secondary = AmberSecondary,
    onSecondary = OnAmberSecondary,
    secondaryContainer = AmberSecondaryContainer,
    onSecondaryContainer = OnAmberSecondaryContainer,
    tertiary = CobaltTertiary,
    onTertiary = OnCobaltTertiary,
    tertiaryContainer = CobaltTertiaryContainer,
    onTertiaryContainer = OnCobaltTertiaryContainer,
    background = LedgerBackgroundLight,
    onBackground = OnLedgerBackgroundLight,
    surface = LedgerSurfaceLight,
    onSurface = OnLedgerSurfaceLight,
    surfaceVariant = LedgerSurfaceVariantLight,
    onSurfaceVariant = OnLedgerSurfaceVariantLight,
    outline = LedgerOutlineLight,
    outlineVariant = LedgerOutlineVariantLight,
    error = LedgerErrorLight,
    onError = OnLedgerErrorLight,
    errorContainer = LedgerErrorContainerLight,
    onErrorContainer = OnLedgerErrorContainerLight
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ElemensysGstTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
