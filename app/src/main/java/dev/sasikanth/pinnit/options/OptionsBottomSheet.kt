package dev.sasikanth.pinnit.options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.utils.PinnitPreferences
import kotlinx.android.synthetic.main.options_bottom_sheet.*
import javax.inject.Inject

class OptionsBottomSheet : BottomSheetDialogFragment() {

  @Inject
  lateinit var pinnitPreferences: PinnitPreferences

  private val optionItemsList = mutableListOf<OptionItem>()
  private val themeButtonCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
    if (isChecked) {
      when (checkedId) {
        R.id.darkModeOn -> pinnitPreferences.changeTheme(PinnitPreferences.Theme.DARK)
        R.id.darkModeOff -> pinnitPreferences.changeTheme(PinnitPreferences.Theme.LIGHT)
        R.id.darkModeAuto -> pinnitPreferences.changeTheme(PinnitPreferences.Theme.AUTO)
      }
      dismiss()
    }
  }
  private val optionsAdapter = OptionsItemAdapter { optionItem ->
    onOptionClicked?.invoke(optionItem)
    dismiss()
  }

  private var onOptionClicked: OnOptionClicked? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    return layoutInflater.inflate(R.layout.options_bottom_sheet, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    optionsList.adapter = optionsAdapter
    optionsAdapter.addItems(optionItemsList)

    when (pinnitPreferences.theme) {
      PinnitPreferences.Theme.AUTO -> themeButtonGroup.check(R.id.darkModeAuto)
      PinnitPreferences.Theme.LIGHT -> themeButtonGroup.check(R.id.darkModeOff)
      PinnitPreferences.Theme.DARK -> themeButtonGroup.check(R.id.darkModeOn)
    }
    themeButtonGroup.addOnButtonCheckedListener(themeButtonCheckedListener)
  }

  override fun onDestroyView() {
    onOptionClicked = null
    themeButtonGroup.removeOnButtonCheckedListener(themeButtonCheckedListener)
    super.onDestroyView()
  }

  fun addOptions(vararg option: OptionItem): OptionsBottomSheet {
    optionItemsList.addAll(option)
    return this
  }

  fun setOnOptionSelectedListener(onOptionClicked: OnOptionClicked): OptionsBottomSheet {
    this.onOptionClicked = onOptionClicked
    return this
  }

  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, javaClass.simpleName)
  }
}