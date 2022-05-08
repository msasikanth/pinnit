package dev.sasikanth.pinnit.notifications

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.theme.PinnitTheme
import dev.sasikanth.pinnit.utils.PinnitDateTimeFormatter
import dev.sasikanth.pinnit.utils.RealUserClock
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun NotificationCard(
  modifier: Modifier = Modifier,
  notification: PinnitNotification,
  pinnitDateTimeFormatter: PinnitDateTimeFormatter,
  onClick: () -> Unit,
  onTogglePinClick: () -> Unit,
  onEditScheduleClick: () -> Unit,
  onRemoveScheduleClick: () -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick.invoke() },
    elevation = 0.dp,
    shape = RoundedCornerShape(0.dp),
    backgroundColor = PinnitTheme.colors.background
  ) {
    ConstraintLayout {
      val (infoHeader, pinButton, title, content, scheduleButton) = createRefs()
      val startGuideline = createGuidelineFromStart(24.dp)
      val topGuideline = createGuidelineFromTop(16.dp)
      val endGuideline = createGuidelineFromEnd(24.dp)
      val bottomGuideline = createGuidelineFromBottom(24.dp)

      InfoHeader(
        modifier = Modifier
          .constrainAs(infoHeader) {
            top.linkTo(topGuideline)
            start.linkTo(startGuideline)
            end.linkTo(pinButton.start)
            width = Dimension.fillToConstraints
          },
        timestamp = pinnitDateTimeFormatter.relativeTimeStamp(notification.updatedAt)
      )

      PinIconButton(
        modifier = Modifier
          .constrainAs(pinButton) {
            end.linkTo(parent.end)
            top.linkTo(parent.top)
          }
      ) {
        onTogglePinClick.invoke()
      }

      NotificationTitle(
        modifier = Modifier
          .constrainAs(title) {
            start.linkTo(startGuideline)
            end.linkTo(pinButton.start)
            top.linkTo(infoHeader.bottom, margin = 8.dp)
            bottom.linkTo(content.top)
            width = Dimension.fillToConstraints
          },
        title = notification.title
      )

      NotificationContent(
        modifier = Modifier
          .constrainAs(content) {
            val bottomConstraint = if (notification.schedule != null) {
              scheduleButton.top
            } else {
              bottomGuideline
            }

            visibility = if (!notification.content.isNullOrBlank()) {
              Visibility.Visible
            } else {
              Visibility.Gone
            }
            start.linkTo(startGuideline)
            end.linkTo(endGuideline)
            top.linkTo(title.bottom)
            bottom.linkTo(bottomConstraint)
            width = Dimension.fillToConstraints
          },
        content = notification.content.orEmpty()
      )

      val scheduleButtonText = if (notification.schedule != null) {
        pinnitDateTimeFormatter.dateAndTimeString(
          date = notification.schedule.scheduleDate!!,
          time = notification.schedule.scheduleTime!!
        )
      } else {
        ""
      }

      ScheduleButton(
        modifier = Modifier
          .constrainAs(scheduleButton) {
            visibility = if (notification.schedule != null) {
              Visibility.Visible
            } else {
              Visibility.Gone
            }
            start.linkTo(startGuideline)
            top.linkTo(content.bottom, margin = 16.dp, goneMargin = 16.dp)
            end.linkTo(endGuideline)
            bottom.linkTo(bottomGuideline)
            width = Dimension.fillToConstraints
          },
        text = scheduleButtonText,
        onEditScheduleClick = onEditScheduleClick,
        onRemoveScheduleClick = onRemoveScheduleClick
      )
    }
  }
}

@Composable
private fun InfoHeader(
  modifier: Modifier = Modifier,
  timestamp: String
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Image(
      modifier = Modifier.size(18.dp),
      painter = painterResource(id = R.drawable.ic_pinnit_app_icon),
      contentDescription = null,
      colorFilter = ColorFilter.tint(PinnitTheme.colors.secondary)
    )

    Text(
      text = stringResource(id = R.string.app_name),
      style = PinnitTheme.typography.overline1,
      color = PinnitTheme.colors.secondary
    )

    Text(
      text = stringResource(id = R.string.dot_separator),
      style = PinnitTheme.typography.overline2,
      color = PinnitTheme.colors.onBackgroundVariant
    )

    Text(
      text = timestamp.uppercase(),
      style = PinnitTheme.typography.overline2,
      color = PinnitTheme.colors.onBackgroundVariant
    )
  }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun PinIconButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val animatedPinVectorDrawable = AnimatedImageVector.animatedVectorResource(id = R.drawable.avd_pin_to_pinned)
  var atEnd by remember { mutableStateOf(false) }

  IconButton(
    modifier = Modifier
      .size(64.dp)
      .then(modifier),
    onClick = {
      atEnd = !atEnd
      onClick.invoke()
    },
  ) {
    Icon(
      painter = rememberAnimatedVectorPainter(animatedImageVector = animatedPinVectorDrawable, atEnd = atEnd),
      contentDescription = null,
      tint = PinnitTheme.colors.secondary
    )
  }
}

@Composable
fun NotificationTitle(
  modifier: Modifier = Modifier,
  title: String
) {
  Text(
    modifier = modifier,
    text = title,
    style = PinnitTheme.typography.subtitle1,
    color = PinnitTheme.colors.onBackground
  )
}

@Composable
fun NotificationContent(
  modifier: Modifier = Modifier,
  content: String
) {
  Text(
    modifier = modifier,
    text = content,
    style = PinnitTheme.typography.body2,
    color = PinnitTheme.colors.onBackgroundVariant
  )
}

@Composable
fun ScheduleButton(
  modifier: Modifier = Modifier,
  text: String,
  onEditScheduleClick: () -> Unit,
  onRemoveScheduleClick: () -> Unit
) {
  var dropdownExpanded by remember { mutableStateOf(false) }

  Box(
    modifier = modifier.wrapContentSize(Alignment.TopStart)
  ) {
    Row(
      modifier = Modifier
        .height(32.dp)
        .wrapContentWidth()
        .border(1.dp, PinnitTheme.colors.secondary)
        .background(Color.Unspecified, shape = RoundedCornerShape(2.dp))
        .clickable { dropdownExpanded = !dropdownExpanded },
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.width(8.dp))
      Icon(
        painter = painterResource(id = R.drawable.ic_pinnit_date),
        contentDescription = null,
        modifier = Modifier.size(18.dp),
        tint = PinnitTheme.colors.secondary
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = text,
        style = PinnitTheme.typography.button,
        color = PinnitTheme.colors.secondary
      )
      Spacer(modifier = Modifier.width(12.dp))
    }

    DropdownMenu(
      expanded = dropdownExpanded,
      onDismissRequest = { dropdownExpanded = false },
      offset = DpOffset(x = 0.dp, y = (-40).dp)
    ) {
      DropdownMenuItem(onClick = {
        dropdownExpanded = false
        onEditScheduleClick.invoke()
      }) {
        Text(
          text = stringResource(id = R.string.notification_menu_edit_schedule),
          style = PinnitTheme.typography.subtitle1
        )
      }

      DropdownMenuItem(onClick = {
        dropdownExpanded = false
        onRemoveScheduleClick.invoke()
      }) {
        Text(
          text = stringResource(id = R.string.notification_menu_remove_schedule),
          style = PinnitTheme.typography.subtitle1
        )
      }
    }
  }
}

@Preview
@Composable
fun NotificationCardUnPinnedPreview() {
  PinnitTheme {
    NotificationCard(
      notification = PinnitNotification(
        uuid = UUID.fromString("4bf5ab27-088c-4d75-9616-197b589a5d9e"),
        title = "This is a reminder title",
        content = "This is a reminder content",
        isPinned = false,
        createdAt = Instant.parse("2018-01-01T00:00:00Z"),
        deletedAt = null,
        updatedAt = Instant.parse("2018-01-01T00:00:00Z"),
        schedule = null
      ),
      pinnitDateTimeFormatter = PinnitDateTimeFormatter(
        userClock = RealUserClock(UTC),
        dateFormatter = DateTimeFormatter.ISO_DATE,
        timeFormatter = DateTimeFormatter.ISO_TIME
      ),
      onClick = {

      },
      onTogglePinClick = {

      },
      onEditScheduleClick = {

      },
      onRemoveScheduleClick = {

      }
    )
  }
}
