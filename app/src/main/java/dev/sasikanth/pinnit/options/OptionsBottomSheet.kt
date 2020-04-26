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
import dev.sasikanth.pinnit.data.PinnitPreferences
import dev.sasikanth.pinnit.di.injector
import kotlinx.android.synthetic.main.theme_selection_sheet.*
import javax.inject.Inject

class OptionsBottomSheet : BottomSheetDialogFragment() {

  @Inject
  lateinit var preferences: PinnitPreferences

  private val themeButtonCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
    if (isChecked) {
      when (checkedId) {
        R.id.darkModeOn -> preferences.changeTheme(PinnitPreferences.Theme.DARK)
        R.id.darkModeOff -> preferences.changeTheme(PinnitPreferences.Theme.LIGHT)
        R.id.darkModeAuto -> preferences.changeTheme(PinnitPreferences.Theme.AUTO)
      }
      dismiss()
    }
  }

  companion object {
    private const val TAG = "OptionsBottomSheet"

    fun show(fragmentManager: FragmentManager) {
      OptionsBottomSheet()
        .show(fragmentManager, TAG)
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.theme_selection_sheet, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    when (preferences.theme) {
      PinnitPreferences.Theme.AUTO -> themeButtonGroup.check(R.id.darkModeAuto)
      PinnitPreferences.Theme.LIGHT -> themeButtonGroup.check(R.id.darkModeOff)
      PinnitPreferences.Theme.DARK -> themeButtonGroup.check(R.id.darkModeOn)
    }
    themeButtonGroup.addOnButtonCheckedListener(themeButtonCheckedListener)
  }

  override fun onDestroyView() {
    themeButtonGroup.removeOnButtonCheckedListener(themeButtonCheckedListener)
    super.onDestroyView()
  }
}
