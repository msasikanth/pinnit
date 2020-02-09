package dev.sasikanth.pinnit.shared

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.pages.historynotifs.HistoryAdapter

class ItemTouchHelperCallback(
    private val context: Context,
    private val onItemSwiped: (pinnitItem: PinnitItem?) -> Unit
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

  override fun getSwipeDirs(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder
  ): Int {
    if (viewHolder is HistoryAdapter.PinnitItemViewHolder) {
      if (viewHolder.pinnitItem.isPinned) {
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
    if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
      if (viewHolder is HistoryAdapter.PinnitItemViewHolder) {
        onItemSwiped(viewHolder.pinnitItem)
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
    val colorDrawable = ColorDrawable(
        context.resolveColor(attrRes = R.attr.colorRowBackground)
    )
    val icon = ContextCompat.getDrawable(context, R.drawable.ic_pinnit_delete)
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
