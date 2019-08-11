package dev.sasikanth.notif.pages.apps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.notif.data.AppItem
import dev.sasikanth.notif.databinding.AppItemBinding

object AppItemDiff : DiffUtil.ItemCallback<AppItem>() {
    override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
        return oldItem == newItem
    }
}

class AppsAdapter(
    private val appItemListener: AppItemListener
) : ListAdapter<AppItem, RecyclerView.ViewHolder>(AppItemDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AppViewHolder) {
            holder.bind(appItem = getItem(position), appItemListener = appItemListener)
        }
    }

    class AppViewHolder private constructor(private val binding: AppItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): AppViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AppItemBinding.inflate(layoutInflater, parent, false)
                return AppViewHolder(binding)
            }
        }

        fun bind(appItem: AppItem, appItemListener: AppItemListener) {
            binding.appItem = appItem
            binding.appItemListener = appItemListener
            binding.executePendingBindings()
        }
    }
}

class AppItemListener(val onClick: (appItem: AppItem) -> Unit) {
    fun click(appItem: AppItem) {
        onClick(appItem)
    }
}
