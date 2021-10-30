package dev.sasikanth.pinnit.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.datastore.core.DataStore
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.databinding.ThemeSelectionSheetBinding
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import reactivecircus.flowbinding.material.buttonCheckedChanges
import javax.inject.Inject

@AndroidEntryPoint
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

  private var _binding: ThemeSelectionSheetBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = ThemeSelectionSheetBinding.inflate(layoutInflater, container, false)
    return _binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      val theme = withContext(dispatcherProvider.io) {
        appPreferencesStore.data.first().theme
      }
      checkThemeSelection(theme)

      binding.themeButtonGroup
        .buttonCheckedChanges()
        .filter { it.checked }
        .map { it.checkedId }
        .collect { updateTheme(it) }
    }
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

  private fun checkThemeSelection(theme: AppPreferences.Theme) {
    val id = when (theme) {
      AppPreferences.Theme.AUTO -> R.id.darkModeAuto
      AppPreferences.Theme.LIGHT -> R.id.darkModeOff
      AppPreferences.Theme.DARK -> R.id.darkModeOn
      else -> R.id.darkModeAuto
    }

    binding.themeButtonGroup.check(id)
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
