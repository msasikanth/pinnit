package dev.sasikanth.notif.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sasikanth.notif.databinding.OptionItemBinding
import dev.sasikanth.notif.databinding.OptionsBottomSheetBinding

data class Option(
    val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val isToggleChecked: Boolean = false,
    val isToggleEnabled: Boolean = false
)

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

    class OnOptionSelected(private val optionSelected: (option: Option) -> Unit) {
        fun onOptionSelected(option: Option) {
            optionSelected(option)
        }
    }

    private inner class OptionsRecyclerView(private val onOptionSelected: OnOptionSelected) :
        RecyclerView.Adapter<OptionItemViewHolder>() {

        private var options: List<Option> = emptyList()

        fun addItems(options: List<Option>) {
            this.options = options
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionItemViewHolder {
            return OptionItemViewHolder.from(parent)
        }

        override fun getItemCount(): Int {
            return options.size
        }

        override fun onBindViewHolder(holder: OptionItemViewHolder, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                holder.bind(options[position], onOptionSelected)
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

        fun bind(option: Option, onOptionSelected: OnOptionSelected) {
            binding.option = option
            binding.onOptionSelected = onOptionSelected
            binding.executePendingBindings()
        }
    }
}
