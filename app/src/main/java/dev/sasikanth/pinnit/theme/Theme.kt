package dev.sasikanth.pinnit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFontFamilyResolver

@Composable
fun PinnitTheme(
  isSystemInDarkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  var preloadedFonts by remember { mutableStateOf(false) }
  val colors = if (isSystemInDarkMode) {
    darkColors()
  } else {
    lightColors()
  }

  val fontFamilyResolver = LocalFontFamilyResolver.current
  LaunchedEffect(key1 = Unit) {
    fontFamilyResolver.preload(baiJamFontFamily)
    fontFamilyResolver.preload(juraFontFamily)
    preloadedFonts = true
  }

  val typography = pinnitTypography()

  CompositionLocalProvider(
    LocalPinnitColors provides colors,
    LocalPinnitTypography provides typography
  ) {
    MaterialTheme {
      if (preloadedFonts) {
        content.invoke()
      }
    }
  }
}

object PinnitTheme {
  val colors: PinnitColors
    @Composable
    get() = LocalPinnitColors.current

  val typography: PinnitTypography
    @Composable
    get() = LocalPinnitTypography.current
}
