package com.agon.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class SalonPalette(val label: String, val story: String) {
    LEONE_FLAG("Leone Flag", "Green land, white peace, blue Atlantic"),
    BO_LATERITE("Bo Laterite", "Red earth roads of Bo City"),
    COTTON_TREE("Cotton Tree", "The old tree that watched Freetown grow"),
    HARBOUR("Queen Elizabeth II Quay", "Harbour light and Atlantic gold"),
    GOLD_LION("Gold Lion", "Krio pride and mountain gold"),
    NIGHT_MARKET("Night Market", "Gara cloth under lantern fire"),
}

data class PaletteSwatch(val a: Color, val b: Color, val c: Color)

fun SalonPalette.swatch(): PaletteSwatch = when (this) {
    SalonPalette.LEONE_FLAG -> PaletteSwatch(FlagGreen, FlagWhite, FlagBlue)
    SalonPalette.BO_LATERITE -> PaletteSwatch(Laterite, LeoneGold, ForestCanopy)
    SalonPalette.COTTON_TREE -> PaletteSwatch(ForestCanopy, PalmLeaf, Color(0xFF6B4226))
    SalonPalette.HARBOUR -> PaletteSwatch(HarbourBlue, HarbourSand, Color(0xFF1A535C))
    SalonPalette.GOLD_LION -> PaletteSwatch(LeoneGold, Ink, ForestCanopy)
    SalonPalette.NIGHT_MARKET -> PaletteSwatch(NightEmber, NightCloth, LeoneGold)
}

fun SalonPalette.lightScheme(): ColorScheme = when (this) {
    SalonPalette.LEONE_FLAG -> lightColorScheme(
        primary = Color(0xFF157A2A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFB7F0C0),
        onPrimaryContainer = Color(0xFF04210A),
        secondary = FlagBlue,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCDE6F8),
        onSecondaryContainer = Color(0xFF001E30),
        tertiary = Color(0xFF9A7408),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFFE7A3),
        onTertiaryContainer = Color(0xFF241A00),
        background = Color(0xFFF4F8F2),
        onBackground = Ink,
        surface = Color.White,
        onSurface = Ink,
        surfaceVariant = Mist,
        onSurfaceVariant = Color(0xFF3D4A40),
        outline = Color(0xFF6D7A6F),
        error = SoftRed,
    )
    SalonPalette.BO_LATERITE -> lightColorScheme(
        primary = Laterite,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDAD3),
        onPrimaryContainer = Color(0xFF3B0904),
        secondary = Color(0xFF9A7408),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFE7A3),
        onSecondaryContainer = Color(0xFF241A00),
        tertiary = ForestCanopy,
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFC3EDD4),
        onTertiaryContainer = Color(0xFF002112),
        background = Color(0xFFFFF6F1),
        onBackground = Color(0xFF2A1612),
        surface = Color.White,
        onSurface = Color(0xFF2A1612),
        surfaceVariant = Color(0xFFF3E3DC),
        onSurfaceVariant = Color(0xFF53433E),
        outline = Color(0xFF86736D),
        error = SoftRed,
    )
    SalonPalette.COTTON_TREE -> lightColorScheme(
        primary = ForestCanopy,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFBFE9D0),
        onPrimaryContainer = Color(0xFF002112),
        secondary = Color(0xFF6B4226),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF3D7C4),
        onSecondaryContainer = Color(0xFF271008),
        tertiary = PalmLeaf,
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFC8F0D8),
        onTertiaryContainer = Color(0xFF002112),
        background = Color(0xFFF3F7F2),
        onBackground = Color(0xFF101A14),
        surface = Color.White,
        onSurface = Color(0xFF101A14),
        surfaceVariant = Color(0xFFDCE8DE),
        onSurfaceVariant = Color(0xFF3E4A41),
        outline = Color(0xFF6C786F),
        error = SoftRed,
    )
    SalonPalette.HARBOUR -> lightColorScheme(
        primary = HarbourBlue,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC5E8F7),
        onPrimaryContainer = Color(0xFF001E2B),
        secondary = Color(0xFF8A6A22),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF6E3B4),
        onSecondaryContainer = Color(0xFF251A00),
        tertiary = Color(0xFF1A535C),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFBFE6EC),
        onTertiaryContainer = Color(0xFF001F24),
        background = Color(0xFFF3F7FA),
        onBackground = Color(0xFF10181C),
        surface = Color.White,
        onSurface = Color(0xFF10181C),
        surfaceVariant = Color(0xFFD7E4EB),
        onSurfaceVariant = Color(0xFF3D4A51),
        outline = Color(0xFF6C7980),
        error = SoftRed,
    )
    SalonPalette.GOLD_LION -> lightColorScheme(
        primary = Color(0xFF8A6A12),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFE7A0),
        onPrimaryContainer = Color(0xFF241A00),
        secondary = Color(0xFF2C2C2C),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE4E4E4),
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = ForestCanopy,
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFC3EDD4),
        onTertiaryContainer = Color(0xFF002112),
        background = Cream,
        onBackground = Ink,
        surface = Color.White,
        onSurface = Ink,
        surfaceVariant = Color(0xFFEFE4C8),
        onSurfaceVariant = Color(0xFF4E4632),
        outline = Color(0xFF7F765E),
        error = SoftRed,
    )
    SalonPalette.NIGHT_MARKET -> lightColorScheme(
        primary = Color(0xFFB85A24),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDBC8),
        onPrimaryContainer = Color(0xFF341000),
        secondary = Color(0xFF6D247E),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF4D4FA),
        onSecondaryContainer = Color(0xFF2C0036),
        tertiary = Color(0xFF8A6A12),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFFE7A0),
        onTertiaryContainer = Color(0xFF241A00),
        background = Color(0xFFFFF5EE),
        onBackground = Color(0xFF24140C),
        surface = Color.White,
        onSurface = Color(0xFF24140C),
        surfaceVariant = Color(0xFFF3E0D4),
        onSurfaceVariant = Color(0xFF53433B),
        outline = Color(0xFF86736A),
        error = SoftRed,
    )
}

fun SalonPalette.darkScheme(): ColorScheme = when (this) {
    SalonPalette.LEONE_FLAG -> darkColorScheme(
        primary = Color(0xFF86E094),
        onPrimary = Color(0xFF003912),
        primaryContainer = Color(0xFF0B5C22),
        onPrimaryContainer = Color(0xFFB7F0C0),
        secondary = Color(0xFF8FCBEF),
        onSecondary = Color(0xFF00344F),
        secondaryContainer = Color(0xFF004D70),
        onSecondaryContainer = Color(0xFFCDE6F8),
        tertiary = LeoneGold,
        onTertiary = Color(0xFF3B2F00),
        tertiaryContainer = Color(0xFF564400),
        onTertiaryContainer = Color(0xFFFFE7A3),
        background = NightBg,
        onBackground = Color(0xFFE4EEE6),
        surface = NightSurface,
        onSurface = Color(0xFFE4EEE6),
        surfaceVariant = NightCard,
        onSurfaceVariant = Color(0xFFC1CDC4),
        outline = Color(0xFF8B978E),
        error = Color(0xFFFFB4AB),
    )
    SalonPalette.BO_LATERITE -> darkColorScheme(
        primary = Color(0xFFFFB4A4),
        onPrimary = Color(0xFF5C160A),
        primaryContainer = LateriteDeep,
        onPrimaryContainer = Color(0xFFFFDAD3),
        secondary = LeoneGold,
        onSecondary = Color(0xFF3B2F00),
        secondaryContainer = Color(0xFF564400),
        onSecondaryContainer = Color(0xFFFFE7A3),
        tertiary = Color(0xFF95D5B2),
        onTertiary = Color(0xFF003824),
        tertiaryContainer = Color(0xFF145238),
        onTertiaryContainer = Color(0xFFC3EDD4),
        background = Color(0xFF1A100D),
        onBackground = Color(0xFFF8E8E2),
        surface = Color(0xFF261814),
        onSurface = Color(0xFFF8E8E2),
        surfaceVariant = Color(0xFF3A2722),
        onSurfaceVariant = Color(0xFFD8C3BC),
        outline = Color(0xFFA08D87),
        error = Color(0xFFFFB4AB),
    )
    SalonPalette.COTTON_TREE -> darkColorScheme(
        primary = Color(0xFF95D5B2),
        onPrimary = Color(0xFF003824),
        primaryContainer = Color(0xFF0C3B28),
        onPrimaryContainer = Color(0xFFBFE9D0),
        secondary = Color(0xFFE4B899),
        onSecondary = Color(0xFF432110),
        secondaryContainer = Color(0xFF5C3220),
        onSecondaryContainer = Color(0xFFF3D7C4),
        tertiary = Color(0xFF8FDBB0),
        onTertiary = Color(0xFF003824),
        tertiaryContainer = Color(0xFF145238),
        onTertiaryContainer = Color(0xFFC8F0D8),
        background = Color(0xFF0C140F),
        onBackground = Color(0xFFDCE8DE),
        surface = Color(0xFF152019),
        onSurface = Color(0xFFDCE8DE),
        surfaceVariant = Color(0xFF223028),
        onSurfaceVariant = Color(0xFFBCC9BF),
        outline = Color(0xFF87948B),
        error = Color(0xFFFFB4AB),
    )
    SalonPalette.HARBOUR -> darkColorScheme(
        primary = Color(0xFF8FCBEF),
        onPrimary = Color(0xFF00344F),
        primaryContainer = Color(0xFF084E6E),
        onPrimaryContainer = Color(0xFFC5E8F7),
        secondary = HarbourSand,
        onSecondary = Color(0xFF3B2F00),
        secondaryContainer = Color(0xFF564400),
        onSecondaryContainer = Color(0xFFF6E3B4),
        tertiary = Color(0xFF8FD4DE),
        onTertiary = Color(0xFF00363D),
        tertiaryContainer = Color(0xFF0E424A),
        onTertiaryContainer = Color(0xFFBFE6EC),
        background = Color(0xFF0B1216),
        onBackground = Color(0xFFD7E4EB),
        surface = Color(0xFF141D22),
        onSurface = Color(0xFFD7E4EB),
        surfaceVariant = Color(0xFF1E2B32),
        onSurfaceVariant = Color(0xFFB8C8D0),
        outline = Color(0xFF82929A),
        error = Color(0xFFFFB4AB),
    )
    SalonPalette.GOLD_LION -> darkColorScheme(
        primary = LeoneGold,
        onPrimary = Color(0xFF3B2F00),
        primaryContainer = Color(0xFF564400),
        onPrimaryContainer = Color(0xFFFFE7A0),
        secondary = Color(0xFFCFCFCF),
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = Color(0xFF3A3A3A),
        onSecondaryContainer = Color(0xFFE4E4E4),
        tertiary = Color(0xFF95D5B2),
        onTertiary = Color(0xFF003824),
        tertiaryContainer = Color(0xFF145238),
        onTertiaryContainer = Color(0xFFC3EDD4),
        background = Color(0xFF14110A),
        onBackground = Color(0xFFF3E8C8),
        surface = Color(0xFF1E1A12),
        onSurface = Color(0xFFF3E8C8),
        surfaceVariant = Color(0xFF2C271C),
        onSurfaceVariant = Color(0xFFD4C9AB),
        outline = Color(0xFF9A9076),
        error = Color(0xFFFFB4AB),
    )
    SalonPalette.NIGHT_MARKET -> darkColorScheme(
        primary = NightEmber,
        onPrimary = Color(0xFF3B1400),
        primaryContainer = Color(0xFF6E3010),
        onPrimaryContainer = Color(0xFFFFDBC8),
        secondary = Color(0xFFE2B4F0),
        onSecondary = Color(0xFF3C0848),
        secondaryContainer = Color(0xFF551A64),
        onSecondaryContainer = Color(0xFFF4D4FA),
        tertiary = LeoneGold,
        onTertiary = Color(0xFF3B2F00),
        tertiaryContainer = Color(0xFF564400),
        onTertiaryContainer = Color(0xFFFFE7A0),
        background = Color(0xFF160E0A),
        onBackground = Color(0xFFF8E4D6),
        surface = Color(0xFF211610),
        onSurface = Color(0xFFF8E4D6),
        surfaceVariant = Color(0xFF33241C),
        onSurfaceVariant = Color(0xFFD8C3B6),
        outline = Color(0xFFA08D82),
        error = Color(0xFFFFB4AB),
    )
}

val LocalSalonPalette = compositionLocalOf { SalonPalette.LEONE_FLAG }

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    palette: SalonPalette = SalonPalette.LEONE_FLAG,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> palette.darkScheme()
        else -> palette.lightScheme()
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalSalonPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
