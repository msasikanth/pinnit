package dev.sasikanth.pinnit.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme

private val RippleShape = RoundedCornerShape(2.dp)
private val IconButtonSize = 56.dp

@Composable
fun PinnitBottomBarIconButton(
  onClick: () -> Unit,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .clip(RippleShape)
      .clickable(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(color = MaterialTheme.colors.primary)
      )
      .size(IconButtonSize),
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

@Preview
@Composable
fun PreviewPinnitBottomBarIconButton() {
  MdcTheme {
    Surface {
      PinnitBottomBarIconButton(onClick = { /*TODO*/ }) {
        Icon(Icons.Filled.Menu, contentDescription = "")
      }
    }
  }
}
