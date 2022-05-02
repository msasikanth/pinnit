package dev.sasikanth.pinnit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class PinnitColors(
  val primary: Color,
  val primaryVariant: Color,
  val onPrimary: Color,
  val secondary: Color,
  val surface: Color,
  val onSurface: Color,
  val background: Color,
  val onBackground: Color,
  val onBackgroundVariant: Color = onBackground.copy(alpha = 0.7f),
  val rowBackground: Color,
  val divider: Color = onBackground.copy(alpha = 0.15f),
  val buttonRow: Color = secondary.copy(alpha = 0.5f),
  val outlineButtonGroup: Color
)

fun lightColors(): PinnitColors {
  return PinnitColors(
    primary = Color(0xFF4D0033),
    primaryVariant = Color(0xFF3D0029),
    onPrimary = Color.White,
    secondary = Color(0xFF4D0033),
    surface = Color.White,
    onSurface = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    rowBackground = Color(0xFFFFE5F6),
    outlineButtonGroup = Color(0xFF4D0033)
  )
}

fun darkColors(): PinnitColors {
  return PinnitColors(
    primary = Color(0xFF660044),
    primaryVariant = Color(0xFF4D0033),
    onPrimary = Color.White,
    secondary = Color(0xFFE5B3D4),
    surface = Color(0xFF240018),
    onSurface = Color.White,
    background = Color(0xFF14000E),
    onBackground = Color.White,
    rowBackground = Color.Black,
    outlineButtonGroup = Color(0xFFF5D0E9)
  )
}

val LocalPinnitColors = staticCompositionLocalOf { lightColors() }
