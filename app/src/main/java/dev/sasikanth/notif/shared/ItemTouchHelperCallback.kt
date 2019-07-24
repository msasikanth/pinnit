package dev.sasikanth.notif.shared

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.notif.data.NotifItem

class ItemTouchHelperCallback(
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

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null && viewHolder is NotifListAdapter.NotifItemViewHolder) {
            val foregroundView = viewHolder.getNotifContentView()
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder != null && viewHolder is NotifListAdapter.NotifItemViewHolder) {
            val foregroundView = viewHolder.getNotifContentView()
            ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(
                c,
                recyclerView,
                foregroundView,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is NotifListAdapter.NotifItemViewHolder) {
            val foregroundView = viewHolder.getNotifContentView()
            ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
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
        if (viewHolder is NotifListAdapter.NotifItemViewHolder) {
            val foregroundView = viewHolder.getNotifContentView()
            ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                foregroundView,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }
}
