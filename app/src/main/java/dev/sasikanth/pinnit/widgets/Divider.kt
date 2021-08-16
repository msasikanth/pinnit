package dev.sasikanth.pinnit.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Divider(
  modifier: Modifier = Modifier,
  thickness: Dp = 1.dp,
  padding: PaddingValues? = null
) {
  val indentMod = if (padding != null) {
    Modifier.padding(paddingValues = padding)
  } else {
    Modifier
  }

  Box(
    modifier
      .then(indentMod)
      .fillMaxWidth()
      .height(thickness)
      .background(color = MaterialTheme.colors.onBackground.copy(alpha = DividerAlpha))
  )
}

private const val DividerAlpha = 0.15f
