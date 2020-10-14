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
import dev.sasikanth.pinnit.R
import kotlinx.android.synthetic.main.pinnit_oem_warning_dialog.*
import java.util.Locale

class OemWarningDialog : DialogFragment() {

  companion object {
    private const val TAG = "OEM_WARNING_DIALOG"
    private val DONT_KILL_MY_APP_LINK = "https://dontkillmyapp.com/${Build.BRAND.toLowerCase(Locale.US)}"

    fun show(fragmentManager: FragmentManager) {
      OemWarningDialog()
        .show(fragmentManager, TAG)
    }
  }

  private var dialogLayout: View? = null

  @SuppressLint("InflateParams")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    dialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.pinnit_oem_warning_dialog, null)
    return MaterialAlertDialogBuilder(requireContext())
      .setView(dialogLayout)
      .create()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return dialogLayout
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    showInstructionsButton?.setOnClickListener {
      showInstructions()
      dismiss()
    }
  }

  override fun onDestroyView() {
    dialogLayout = null
    super.onDestroyView()
  }

  private fun showInstructions() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DONT_KILL_MY_APP_LINK))
    startActivity(intent)
  }
}
