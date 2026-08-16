package com.agon.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DefaultDarkColorScheme = darkColorScheme(
    primary = SaloneEmeraldLight,
    onPrimary = Color.Black,
    primaryContainer = SaloneEmeraldDark,
    onPrimaryContainer = SaloneEmeraldContainer,
    secondary = SaloneAtlanticBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF003866),
    onSecondaryContainer = SaloneBlueContainer,
    tertiary = SaloneGold,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
)

private val DefaultLightColorScheme = lightColorScheme(
    primary = SaloneEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = SaloneEmeraldContainer,
    onPrimaryContainer = SaloneEmeraldOnContainer,
    secondary = SaloneAtlanticBlue,
    onSecondary = Color.White,
    secondaryContainer = SaloneBlueContainer,
    onSecondaryContainer = SaloneBlueOnContainer,
    tertiary = SaloneGold,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
)

// Blue Variant
private val AtlanticBlueLight = lightColorScheme(
    primary = SaloneAtlanticBlue,
    onPrimary = Color.White,
    primaryContainer = SaloneBlueContainer,
    onPrimaryContainer = SaloneBlueOnContainer,
    secondary = SaloneEmeraldPrimary,
    background = Color(0xFFF6F9FD),
    surface = Color.White
)

private val AtlanticBlueDark = darkColorScheme(
    primary = Color(0xFF70B2FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003D75),
    secondary = SaloneEmeraldLight,
    background = Color(0xFF0D141C),
    surface = Color(0xFF141F2B)
)

// Gold Variant
private val SunsetGoldLight = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = SaloneEmeraldPrimary,
    background = Color(0xFFFFFBF0),
    surface = Color.White
)

private val SunsetGoldDark = darkColorScheme(
    primary = SaloneGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF78350F),
    secondary = SaloneEmeraldLight,
    background = Color(0xFF191308),
    surface = Color(0xFF241C0F)
)

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: SaloneThemeMode = SaloneThemeMode.COTTON_TREE_GREEN,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeMode == SaloneThemeMode.ATLANTIC_BLUE -> {
            if (darkTheme) AtlanticBlueDark else AtlanticBlueLight
        }
        themeMode == SaloneThemeMode.SUNSET_GOLD -> {
            if (darkTheme) SunsetGoldDark else SunsetGoldLight
        }
        themeMode == SaloneThemeMode.FREETOWN_MIDNIGHT -> {
            DefaultDarkColorScheme
        }
        darkTheme -> DefaultDarkColorScheme
        else -> DefaultLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
