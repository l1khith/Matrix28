package com.l1khith.matrix28.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object MatrixColors {
    val Surface = Color(0xFF131316)
    val SurfaceDim = Color(0xFF131316)
    val SurfaceBright = Color(0xFF39393C)
    val SurfaceContainerLowest = Color(0xFF0E0E11)
    val SurfaceContainerLow = Color(0xFF1B1B1E)
    val SurfaceContainer = Color(0xFF1F1F22)
    val SurfaceContainerHigh = Color(0xFF2A2A2D)
    val SurfaceContainerHighest = Color(0xFF353437)

    val OnSurface = Color(0xFFE4E1E5)
    val OnSurfaceVariant = Color(0xFFC2C6D6)
    val InverseSurface = Color(0xFFE4E1E5)
    val InverseOnSurface = Color(0xFF303033)

    val Outline = Color(0xFF8C909F)
    val OutlineVariant = Color(0xFF424754)
    val SurfaceTint = Color(0xFFADC6FF)

    val Primary = Color(0xFFADC6FF)
    val OnPrimary = Color(0xFF002E6A)
    val PrimaryContainer = Color(0xFF4D8EFF)
    val OnPrimaryContainer = Color(0xFF00285D)
    val InversePrimary = Color(0xFF005AC2)

    val Secondary = Color(0xFFFFB95F)
    val OnSecondary = Color(0xFF472A00)
    val SecondaryContainer = Color(0xFFEE9800)
    val OnSecondaryContainer = Color(0xFF5B3800)

    val Tertiary = Color(0xFF4EDEA3)
    val OnTertiary = Color(0xFF003824)
    val TertiaryContainer = Color(0xFF00A572)
    val OnTertiaryContainer = Color(0xFF00311F)

    val Error = Color(0xFFFFB4AB)
    val OnError = Color(0xFF690005)
    val ErrorContainer = Color(0xFF93000A)
    val OnErrorContainer = Color(0xFFFFDAD6)

    val TextHeader = Color(0xFFFAFAFA)
    val TextSecondary = Color(0xFFA1A1AA)
}

object MatrixShapes {
    val Sm = RoundedCornerShape(4.dp)
    val Md = RoundedCornerShape(8.dp)
    val Lg = RoundedCornerShape(16.dp)
    val Xl = RoundedCornerShape(24.dp)
}

val MatrixColorScheme = darkColorScheme(
    primary = MatrixColors.Primary,
    onPrimary = MatrixColors.OnPrimary,
    primaryContainer = MatrixColors.PrimaryContainer,
    onPrimaryContainer = MatrixColors.OnPrimaryContainer,
    secondary = MatrixColors.Secondary,
    onSecondary = MatrixColors.OnSecondary,
    secondaryContainer = MatrixColors.SecondaryContainer,
    onSecondaryContainer = MatrixColors.OnSecondaryContainer,
    tertiary = MatrixColors.Tertiary,
    onTertiary = MatrixColors.OnTertiary,
    tertiaryContainer = MatrixColors.TertiaryContainer,
    onTertiaryContainer = MatrixColors.OnTertiaryContainer,
    error = MatrixColors.Error,
    onError = MatrixColors.OnError,
    errorContainer = MatrixColors.ErrorContainer,
    onErrorContainer = MatrixColors.OnErrorContainer,
    background = MatrixColors.Surface,
    onBackground = MatrixColors.OnSurface,
    surface = MatrixColors.SurfaceContainerLow,
    onSurface = MatrixColors.OnSurface,
    surfaceVariant = MatrixColors.SurfaceContainerHigh,
    onSurfaceVariant = MatrixColors.OnSurfaceVariant,
    outline = MatrixColors.Outline,
    outlineVariant = MatrixColors.OutlineVariant,
    surfaceTint = MatrixColors.SurfaceTint
)
