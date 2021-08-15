package dev.sasikanth.pinnit.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import dev.sasikanth.pinnit.R

private val spacerModifier = Modifier.width(8.dp)
private val EmptyIconWidth = 40.dp

@Composable
fun PinnitBottomBar(
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit,
  content: @Composable () -> Unit,
  actionIcon: (@Composable () -> Unit)?
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .then(modifier)
  ) {
    Divider()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      navigationIcon()
      Spacer(modifier = spacerModifier)
      Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
      ) {
        content()
      }
      Spacer(modifier = spacerModifier)
      if (actionIcon != null) {
        actionIcon()
      } else {
        Spacer(modifier = Modifier.width(EmptyIconWidth))
      }
    }
  }
}

@Preview
@Composable
fun PinnitBottomBarPreview() {
  MdcTheme {
    PinnitBottomBar(
      navigationIcon = {
        PinnitBottomBarIconButton(
          onClick = { /*TODO*/ }
        ) {
          Icon(painterResource(id = R.drawable.ic_pinnit_dark_mode), contentDescription = null)
        }
      },
      content = {
        PinnitButton(
          modifier = Modifier.fillMaxWidth(),
          onClick = { /*TODO*/ }
        ) {
          Text(text = "CREATE")
        }
      },
      actionIcon = {
        PinnitBottomBarIconButton(
          onClick = { /*TODO*/ }
        ) {
          Icon(painterResource(id = R.drawable.ic_pinnit_about), contentDescription = null)
        }
      }
    )
  }
}

@Preview
@Composable
fun PinnitBottomBarWithoutAction() {
  MdcTheme {
    PinnitBottomBar(
      navigationIcon = {
        PinnitBottomBarIconButton(
          onClick = { /*TODO*/ }
        ) {
          Icon(painterResource(id = R.drawable.ic_pinnit_dark_mode), contentDescription = null)
        }
      },
      content = {
        PinnitButton(
          modifier = Modifier.fillMaxWidth(),
          onClick = { /*TODO*/ }
        ) {
          Text(text = "CREATE")
        }
      },
      actionIcon = null
    )
  }
}

@Preview
@Composable
fun PinnitBottomBarWithTextContentAndWithoutAction() {
  MdcTheme {
    PinnitBottomBar(
      navigationIcon = {
        PinnitBottomBarIconButton(
          onClick = { /*TODO*/ }
        ) {
          Icon(Icons.Filled.Menu, contentDescription = null)
        }
      },
      content = {
        Text(
          text = "Only notifications from apps you select will be displayed by Pinnit.",
          style = MaterialTheme.typography.caption
        )
      },
      actionIcon = null
    )
  }
}
