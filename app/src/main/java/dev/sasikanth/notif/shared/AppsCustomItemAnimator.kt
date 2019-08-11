package dev.sasikanth.notif.shared

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class AppsCustomItemAnimator : DefaultItemAnimator() {

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }
}
