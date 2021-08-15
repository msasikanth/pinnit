package dev.sasikanth.pinnit.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import dev.sasikanth.pinnit.R

private val ButtonHeight = 40.dp

@Composable
fun PinnitButton(
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  onClick: () -> Unit,
  content: @Composable RowScope.() -> Unit
) {
  Button(
    modifier = Modifier
      .defaultMinSize(minHeight = ButtonHeight)
      .then(modifier),
    enabled = enabled,
    onClick = onClick,
    elevation = null,
    shape = RoundedCornerShape(dimensionResource(id = R.dimen.shape_theming_radius)),
    content = content
  )
}

@Preview
@Composable
fun PreviewPinnitButton() {
  MdcTheme {
    PinnitButton(onClick = { /*TODO*/ }) {
      Text(text = "SAMPLE")
    }
  }
}

@Preview
@Composable
fun PreviewDisabledPinnitButton() {
  MdcTheme {
    PinnitButton(
      onClick = { /*TODO*/ },
      enabled = false
    ) {
      Text(text = "SAMPLE")
    }
  }
}
