package dev.sasikanth.pinnit.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import dev.sasikanth.pinnit.R
import kotlinx.android.synthetic.main.activity_main.*

class EditorScreen : Fragment(R.layout.fragment_notification_editor) {

  private val args by navArgs<EditorScreenArgs>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val forward = MaterialSharedAxis.create(MaterialSharedAxis.Y, true)
    enterTransition = forward

    val backward = MaterialSharedAxis.create(MaterialSharedAxis.Y, false)
    returnTransition = backward
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    requireActivity().bottomBar.setNavigationIcon(R.drawable.ic_arrow_back)
    requireActivity().bottomBar.setContentActionEnabled(false)
    requireActivity().bottomBar.setContentActionText(R.string.save_and_pin)
    requireActivity().bottomBar.setActionIcon(null)

    requireActivity().bottomBar.setNavigationOnClickListener {
      if (findNavController().currentDestination?.id == R.id.editorScreen) {
        findNavController().navigateUp()
      }
    }
    requireActivity().bottomBar.setContentActionOnClickListener {
      // TODO: Handle save notification
    }
  }
}
