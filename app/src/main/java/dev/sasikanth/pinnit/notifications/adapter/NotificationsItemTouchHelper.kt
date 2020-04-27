package dev.sasikanth.pinnit.notifications.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter.NotificationViewHolder
import dev.sasikanth.pinnit.utils.px
import dev.sasikanth.pinnit.utils.resolveColor

class NotificationsItemTouchHelper(
  private val context: Context,
  private val adapter: NotificationsListAdapter,
  private val onItemSwiped: (notification: PinnitNotification) -> Unit
) : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {

  private val colorDrawable = ColorDrawable(
    context.resolveColor(attrRes = R.attr.colorRowBackground)
  )
  private val icon = ContextCompat.getDrawable(context, R.drawable.ic_pinnit_delete)

  override fun isItemViewSwipeEnabled(): Boolean {
    return true
  }

  override fun isLongPressDragEnabled(): Boolean {
    return false
  }

  override fun getSwipeDirs(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder
  ): Int {
    if (viewHolder is NotificationViewHolder) {
      val notification = adapter.currentList[viewHolder.adapterPosition]
      if (notification.isPinned) {
        return 0
      }
    }
    return super.getSwipeDirs(recyclerView, viewHolder)
  }

  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    return false
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    val adapterPosition = viewHolder.adapterPosition
    if (adapterPosition != RecyclerView.NO_POSITION) {
      if (viewHolder is NotificationViewHolder) {
        val notification = adapter.currentList[adapterPosition]
        onItemSwiped(notification)
      }
    }
  }

  override fun onChildDraw(
    c: Canvas,
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    dX: Float,
    dY: Float,
    actionState: Int,
    isCurrentlyActive: Boolean
  ) {
    val itemView = viewHolder.itemView
    val iconMarginH = 24.px
    val cellHeight = itemView.bottom - itemView.top
    val iconWidth = icon!!.intrinsicWidth
    val iconHeight = icon.intrinsicHeight
    val iconTop = itemView.top + (cellHeight - iconHeight) / 2
    val iconBottom = iconTop + iconHeight

    val iconLeft: Int
    val iconRight: Int

    if (dX > 0) {
      // Right Swipe
      colorDrawable.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
      iconLeft = iconMarginH
      iconRight = iconMarginH + iconWidth
    } else {
      // Left Swipe
      colorDrawable.setBounds(
        itemView.right + dX.toInt(),
        itemView.top,
        itemView.right,
        itemView.bottom
      )
      iconLeft = itemView.right - iconMarginH - iconWidth
      iconRight = itemView.right - iconMarginH
    }

    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

    colorDrawable.draw(c)
    icon.draw(c)

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }
}
