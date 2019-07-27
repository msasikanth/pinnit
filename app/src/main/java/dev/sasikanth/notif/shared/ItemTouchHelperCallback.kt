package dev.sasikanth.notif.shared

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.notif.R
import dev.sasikanth.notif.data.NotifItem

class ItemTouchHelperCallback(
    private val context: Context,
    private val onItemSwiped: (notifItem: NotifItem?) -> Unit
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder is NotifListAdapter.NotifItemViewHolder) {
            if (viewHolder.isPinned()) {
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
            if (viewHolder is NotifListAdapter.NotifItemViewHolder) {
                onItemSwiped(viewHolder.notifItem())
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
        val colorDrawable =
            ColorDrawable(ContextCompat.getColor(context, R.color.notifCardBackgroundOverlay))
        val itemView = viewHolder.itemView
        if (dX > 0) {
            colorDrawable.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        } else {
            colorDrawable.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
        }
        colorDrawable.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
