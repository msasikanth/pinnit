@file:OptIn(ExperimentalTextApi::class)

package dev.sasikanth.pinnit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import dev.sasikanth.pinnit.R

val fontProvider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage = "com.google.android.gms",
  certificates = R.array.com_google_android_gms_fonts_certs
)

val baiJamFontFamily = FontFamily(
  listOf(
    Font(
      googleFont = GoogleFont(name = "Bai Jamjuree"),
      weight = FontWeight.Bold,
      fontProvider = fontProvider
    ),
    Font(
      googleFont = GoogleFont(name = "Bai Jamjuree"),
      weight = FontWeight.SemiBold,
      fontProvider = fontProvider
    ),
    Font(
      googleFont = GoogleFont(name = "Bai Jamjuree"),
      weight = FontWeight.Medium,
      fontProvider = fontProvider
    ),
    Font(
      googleFont = GoogleFont(name = "Bai Jamjuree"),
      weight = FontWeight.Normal,
      fontProvider = fontProvider
    )
  )
)
val juraFontFamily = FontFamily(
  listOf(
    Font(
      googleFont = GoogleFont(name = "Jura"),
      weight = FontWeight.Bold,
      fontProvider = fontProvider
    ),
    Font(
      googleFont = GoogleFont(name = "Jura"),
      weight = FontWeight.SemiBold,
      fontProvider = fontProvider
    )
  )
)

@Immutable
data class PinnitTypography(
  val h3: TextStyle,
  val h4: TextStyle,
  val h5: TextStyle,
  val h6: TextStyle,
  val subtitle1: TextStyle,
  val subtitle2: TextStyle,
  val button: TextStyle,
  val body1: TextStyle,
  val body2: TextStyle,
  val caption: TextStyle,
  val overline1: TextStyle,
  val overline2: TextStyle
)

fun pinnitTypography(): PinnitTypography {
  return PinnitTypography(
    h3 = TextStyle(
      fontSize = 48.sp,
      lineHeight = 64.sp,
      letterSpacing = 0.0.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.Medium
    ),
    h4 = TextStyle(
      fontSize = 34.sp,
      lineHeight = 34.sp,
      letterSpacing = 0.0075.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.SemiBold
    ),
    h5 = TextStyle(
      fontSize = 24.sp,
      lineHeight = 36.sp,
      letterSpacing = 0.0.sp,
      fontFamily = juraFontFamily,
      fontWeight = FontWeight.SemiBold
    ),
    h6 = TextStyle(
      fontSize = 22.sp,
      lineHeight = 32.sp,
      letterSpacing = 0.0.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.SemiBold
    ),
    subtitle1 = TextStyle(
      fontSize = 17.sp,
      lineHeight = 24.sp,
      letterSpacing = 0.0235.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.SemiBold
    ),
    subtitle2 = TextStyle(
      fontSize = 14.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.0357.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.Medium
    ),
    button = TextStyle(
      fontSize = 14.sp,
      lineHeight = 14.sp,
      letterSpacing = 0.07145.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.Bold
    ),
    body1 = TextStyle(
      fontSize = 16.sp,
      lineHeight = 24.sp,
      letterSpacing = 0.0.sp,
      fontFamily = juraFontFamily,
      fontWeight = FontWeight.Bold
    ),
    body2 = TextStyle(
      fontSize = 14.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.0.sp,
      fontFamily = juraFontFamily,
      fontWeight = FontWeight.Bold
    ),
    caption = TextStyle(
      fontSize = 12.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.05835.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.Normal
    ),
    overline1 = TextStyle(
      fontSize = 11.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.137.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.Bold
    ),
    overline2 = TextStyle(
      fontSize = 11.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.137.sp,
      fontFamily = baiJamFontFamily,
      fontWeight = FontWeight.Medium
    )
  )
}

val LocalPinnitTypography = staticCompositionLocalOf { pinnitTypography() }
