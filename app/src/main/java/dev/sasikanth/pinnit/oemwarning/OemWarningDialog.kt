package dev.sasikanth.pinnit.oemwarning

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.sasikanth.pinnit.databinding.PinnitOemWarningDialogBinding

class OemWarningDialog : DialogFragment() {

  companion object {
    private const val TAG = "OEM_WARNING_DIALOG"
    private val DONT_KILL_MY_APP_LINK = "https://dontkillmyapp.com/${Build.BRAND}"

    fun show(fragmentManager: FragmentManager) {
      OemWarningDialog()
        .show(fragmentManager, TAG)
    }
  }

  private var _binding: PinnitOemWarningDialogBinding? = null
  private val binding get() = _binding!!

  @SuppressLint("InflateParams")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = PinnitOemWarningDialogBinding.inflate(layoutInflater, null, false)

    return MaterialAlertDialogBuilder(requireContext())
      .setView(binding.root)
      .create()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.showInstructionsButton.setOnClickListener {
      showInstructions()
      dismiss()
    }
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

  private fun showInstructions() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DONT_KILL_MY_APP_LINK))
    startActivity(intent)
  }
}
