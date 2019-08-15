package dev.sasikanth.pinnit.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.databinding.OptionItemBinding
import dev.sasikanth.pinnit.databinding.OptionsBottomSheetBinding

private const val OPTION_ITEM = 1
private const val OPTION_SEPARATOR = 2

sealed class Option {
    data class OptionItem(
        val id: Int,
        @StringRes val title: Int,
        @DrawableRes val icon: Int,
        val isToggleChecked: Boolean = false,
        val isToggleEnabled: Boolean = false,
        val isSelected: Boolean = false
    ) : Option()

    object OptionSeparator : Option()
}

class OptionsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "OptionsBottomSheet"
    }

    private val optionsList = mutableListOf<Option>()

    private lateinit var binding: OptionsBottomSheetBinding
    private var onOptionSelected: OnOptionSelected? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OptionsBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = OptionsRecyclerView(OnOptionSelected {
            onOptionSelected?.onOptionSelected(it)
            dismiss()
        })
        binding.optionsList.adapter = adapter

        adapter.addItems(optionsList)
    }

    fun addOption(vararg option: Option): OptionsBottomSheet {
        optionsList.addAll(option)
        return this
    }

    fun setOnOptionSelectedListener(onOptionSelected: OnOptionSelected): OptionsBottomSheet {
        this.onOptionSelected = onOptionSelected
        return this
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    class OnOptionSelected(private val optionSelected: (option: Option.OptionItem) -> Unit) {
        fun onOptionSelected(option: Option.OptionItem) {
            optionSelected(option)
        }
    }

    private inner class OptionsRecyclerView(private val onOptionSelected: OnOptionSelected) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var options: List<Any> = emptyList()

        fun addItems(options: List<Any>) {
            this.options = options
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == OPTION_ITEM) {
                OptionItemViewHolder.from(parent)
            } else {
                OptionItemSeparator.from(parent)
            }
        }

        override fun getItemCount(): Int {
            return options.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (options[position] is Option.OptionItem) {
                OPTION_ITEM
            } else {
                OPTION_SEPARATOR
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                if (holder is OptionItemViewHolder) {
                    holder.bind(options[position] as Option.OptionItem, onOptionSelected)
                }
            }
        }
    }

    private class OptionItemSeparator private constructor(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        companion object {

            fun from(parent: ViewGroup): OptionItemSeparator {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.option_item_separator, parent, false)
                return OptionItemSeparator(view)
            }
        }
    }

    private class OptionItemViewHolder private constructor(private val binding: OptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {

            fun from(parent: ViewGroup): OptionItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OptionItemBinding.inflate(layoutInflater, parent, false)
                return OptionItemViewHolder(binding)
            }
        }

        init {
            binding.toggle.setOnCheckedChangeListener { _, isChecked ->
                val option = binding.option
                if (option != null) {
                    if (option.isToggleChecked != isChecked) {
                        binding.onOptionSelected?.onOptionSelected(option)
                    }
                }
            }
        }

        fun bind(option: Option.OptionItem, onOptionSelected: OnOptionSelected) {
            binding.option = option
            binding.onOptionSelected = onOptionSelected
            binding.executePendingBindings()
        }
    }
}
