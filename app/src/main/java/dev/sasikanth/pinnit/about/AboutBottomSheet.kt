package dev.sasikanth.pinnit.about

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sasikanth.pinnit.BuildConfig
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.databinding.SheetAboutBinding
import dev.sasikanth.pinnit.oemwarning.OemWarningDialog
import dev.sasikanth.pinnit.oemwarning.shouldShowWarningForOEM

class AboutBottomSheet : BottomSheetDialogFragment() {

  companion object {
    private const val TAG = "ABOUT_BOTTOM_SHEET"
    private const val PINNIT_PROJECT_URL = "https://github.com/msasikanth/pinnit"

    fun show(fragmentManager: FragmentManager) {
      AboutBottomSheet().show(fragmentManager, TAG)
    }
  }

  private var _binding: SheetAboutBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = SheetAboutBinding.inflate(layoutInflater, container, false)
    return _binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setAppVersion()

    //    binding.contactSupportButton.setOnClickListener { sendSupportEmail() }
    binding.sourceCodeButton.setOnClickListener { openGitHubProject() }

    setupShowOemWarning()
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

  private fun setupShowOemWarning() {
    val brandName = Build.BRAND
    val shouldShowOemWarning = shouldShowWarningForOEM(brandName)
    binding.dividerView.isVisible = shouldShowOemWarning
    binding.oemWarningButton.isVisible = shouldShowOemWarning

    binding.oemWarningButton.setOnClickListener {
      dismiss()
      OemWarningDialog.show(requireActivity().supportFragmentManager)
    }
  }

  private fun setAppVersion() {
    val versionName = BuildConfig.VERSION_NAME
    binding.appVersionTextView.text = getString(R.string.app_version, versionName)
  }

  private fun openGitHubProject() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PINNIT_PROJECT_URL))
    startActivity(intent)
  }
}
