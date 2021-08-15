package dev.sasikanth.pinnit.options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.datastore.core.DataStore
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.android.synthetic.main.theme_selection_sheet.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import reactivecircus.flowbinding.material.buttonCheckedChanges
import javax.inject.Inject

class OptionsBottomSheet : BottomSheetDialogFragment() {

  @Inject
  lateinit var appPreferencesStore: DataStore<AppPreferences>

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      val theme = withContext(dispatcherProvider.io) {
        appPreferencesStore.data.first().theme
      }
      checkThemeSelection(theme)

      themeButtonGroup
        .buttonCheckedChanges()
        .filter { it.checked }
        .map { it.checkedId }
        .collect { updateTheme(it) }
    }
  }

  private fun checkThemeSelection(theme: AppPreferences.Theme) {
    when (theme) {
      AppPreferences.Theme.AUTO -> themeButtonGroup.check(R.id.darkModeAuto)
      AppPreferences.Theme.LIGHT -> themeButtonGroup.check(R.id.darkModeOff)
      AppPreferences.Theme.DARK -> themeButtonGroup.check(R.id.darkModeOn)
      else -> themeButtonGroup.check(R.id.darkModeAuto)
    }
  }

  private suspend fun updateTheme(@IdRes checkedId: Int) {
    val theme = when (checkedId) {
      R.id.darkModeOn -> AppPreferences.Theme.DARK
      R.id.darkModeOff -> AppPreferences.Theme.LIGHT
      R.id.darkModeAuto -> AppPreferences.Theme.AUTO
      else -> throw IllegalArgumentException("Unknown theme selection")
    }

    appPreferencesStore.updateData { currentData ->
      currentData.toBuilder()
        .setTheme(theme)
        .build()
    }

    dismiss()
  }
}
