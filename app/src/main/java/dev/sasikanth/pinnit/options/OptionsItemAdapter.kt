package dev.sasikanth.pinnit.options

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.pinnit.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.option_item.*

typealias OnOptionClicked = (OptionItem) -> Unit

class OptionsItemAdapter(
    private val onOptionClicked: OnOptionClicked
) : RecyclerView.Adapter<OptionItemViewHolder>() {

  private var options: List<OptionItem> = emptyList()

  fun addItems(options: List<OptionItem>) {
    this.options = options
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionItemViewHolder {
    return OptionItemViewHolder.from(parent).apply {
      itemView.setOnClickListener {
        onOptionClicked(optionItem)
      }
    }
  }

  override fun getItemCount(): Int {
    return options.size
  }

  override fun onBindViewHolder(holder: OptionItemViewHolder, position: Int) {
    if (position != RecyclerView.NO_POSITION) {
      holder.optionItem = options[position]
      holder.render()
    }
  }
}

class OptionItemViewHolder private constructor(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

  lateinit var optionItem: OptionItem

  fun render() {
    if (optionItem.isSelected) {
      optionRoot.setBackgroundResource(R.drawable.option_item_selected)
    } else {
      optionRoot.background = null
    }

    optionIcon.setImageResource(optionItem.icon)
    optionIcon.isSelected = optionItem.isSelected

    optionLabel.setText(optionItem.title)
    optionLabel.isSelected = optionItem.isSelected
  }

  companion object {

    fun from(parent: ViewGroup): OptionItemViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding = layoutInflater.inflate(R.layout.option_item, parent, false)
      return OptionItemViewHolder(binding)
    }
  }
}