package dev.sasikanth.pinnit.pages.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.pinnit.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.app_item_layout.*

typealias ToggleAppSelection = (AppItem) -> Unit

class AppsAdapter(
    private val toggleAppSelection: ToggleAppSelection
) : ListAdapter<AppItem, AppsAdapter.AppItemViewHolder>(AppsDiff) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppItemViewHolder =
      AppItemViewHolder.from(parent).apply {
        appCard.setOnClickListener {
          val appItem = getItem(adapterPosition)

          appCard.isChecked = !appItem.isSelected
          toggleAppSelection(appItem)
        }
      }

  override fun onBindViewHolder(holder: AppItemViewHolder, position: Int) {
    val appItem = getItem(position)
    holder.bind(appItem)
  }

  class AppItemViewHolder private constructor(
      override val containerView: View
  ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
      fun from(parent: ViewGroup): AppItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item_layout, parent, false)
        return AppItemViewHolder(view)
      }
    }

    init {
      appCard.setOnCheckedChangeListener { _, isChecked ->
        appLabel.isSelected = isChecked
      }
    }

    fun bind(appItem: AppItem) {
      appIcon.setImageDrawable(appItem.icon)
      appLabel.text = appItem.appName
      appCard.isChecked = appItem.isSelected
    }
  }
}

object AppsDiff : DiffUtil.ItemCallback<AppItem>() {
  override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
    return oldItem.packageName == newItem.packageName
  }

  override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
    return oldItem == newItem
  }
}
