package com.agon.app.ui.theme

import androidx.compose.ui.graphics.Color

// Salone Brand Colors (Emerald, Atlantic Blue, Gold, Pristine White)
val SaloneEmeraldPrimary = Color(0xFF008751) // Sierra Leone Flag Green
val SaloneEmeraldDark = Color(0xFF005C37)
val SaloneEmeraldLight = Color(0xFF33B679)
val SaloneEmeraldContainer = Color(0xFFD0F8E3)
val SaloneEmeraldOnContainer = Color(0xFF003920)

val SaloneAtlanticBlue = Color(0xFF0066B2) // Sierra Leone Flag Blue
val SaloneBlueContainer = Color(0xFFD4E8FF)
val SaloneBlueOnContainer = Color(0xFF002E5C)

val SaloneGold = Color(0xFFFBB034)
val SaloneGoldContainer = Color(0xFFFFF0D4)

// Light Theme Neutrals
val LightBackground = Color(0xFFF8FAF9)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F4F2)
val LightOnSurface = Color(0xFF191C1B)
val LightOnSurfaceVariant = Color(0xFF3F4945)
val LightOutline = Color(0xFFBFC9C3)

// Dark Theme Neutrals
val DarkBackground = Color(0xFF0F1512)
val DarkSurface = Color(0xFF17201C)
val DarkSurfaceVariant = Color(0xFF222D28)
val DarkOnSurface = Color(0xFFE1E4E1)
val DarkOnSurfaceVariant = Color(0xFFC0C8C3)
val DarkOutline = Color(0xFF5A6660)

// Chat specific colors
val ChatBubbleSentLight = Color(0xFFE0F7EB)
val ChatBubbleSentDark = Color(0xFF004D30)
val ChatBubbleReceivedLight = Color(0xFFFFFFFF)
val ChatBubbleReceivedDark = Color(0xFF232D28)
val ChatOnlineGreen = Color(0xFF10B981)
val ChatDoubleCheckBlue = Color(0xFF3B82F6)

// Custom Themes for Salon Na We Yon
enum class SaloneThemeMode {
    COTTON_TREE_GREEN,
    ATLANTIC_BLUE,
    SUNSET_GOLD,
    FREETOWN_MIDNIGHT
}
