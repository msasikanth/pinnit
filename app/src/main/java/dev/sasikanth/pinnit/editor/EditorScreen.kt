package dev.sasikanth.pinnit.editor

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Function
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.di.injector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notification_editor.*
import java.util.UUID
import javax.inject.Inject

class EditorScreen : Fragment(R.layout.fragment_notification_editor), EditorScreenUi {

  @Inject
  lateinit var effectHandler: EditorScreenEffectHandler.Factory

  private val args by navArgs<EditorScreenArgs>()

  private val uiRender = EditorScreenUiRender(this)

  private val viewModel: MobiusLoopViewModel<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect> by viewModels {
    object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val notificationUuid = if (args.notificationUuid != null) {
          UUID.fromString(args.notificationUuid)
        } else {
          null
        }
        return MobiusLoopViewModel.create<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect>(
          Function { viewEffectConsumer ->
            Mobius.loop(
              EditorScreenUpdate(),
              effectHandler.create(viewEffectConsumer)
            )
          },
          EditorScreenModel.default(notificationUuid = notificationUuid),
          EditorScreenInit()
        ) as T
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    injector.inject(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val forward = MaterialSharedAxis.create(MaterialSharedAxis.Y, true)
    enterTransition = forward

    val backward = MaterialSharedAxis.create(MaterialSharedAxis.Y, false)
    returnTransition = backward

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      viewModel.dispatchEvent(BackClicked)
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel.models.observe(viewLifecycleOwner, Observer { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(viewLifecycleOwner, Observer { viewEffect ->
      when (viewEffect) {
        is SetTitle -> {
          setTitleText(viewEffect.title)
        }

        is SetContent -> {
          setContentText(viewEffect.content)
        }

        CloseEditorView -> {
          closeEditor()
        }

        ShowConfirmExitEditorDialog -> {
          showConfirmExitDialog()
        }
      }
    })

    requireActivity().bottomBar.setNavigationIcon(R.drawable.ic_arrow_back)
    requireActivity().bottomBar.setContentActionEnabled(false)
    requireActivity().bottomBar.setContentActionText(contentActionText = null)
    requireActivity().bottomBar.setActionIcon(null)

    requireActivity().bottomBar.setNavigationOnClickListener {
      viewModel.dispatchEvent(BackClicked)
    }
    requireActivity().bottomBar.setContentActionOnClickListener {
      viewModel.dispatchEvent(SaveClicked)
    }

    editorScrollView.applySystemWindowInsetsToPadding(bottom = true)

    titleEditText.doAfterTextChanged { viewModel.dispatchEvent(TitleChanged(it?.toString().orEmpty())) }
    contentEditText.doAfterTextChanged { viewModel.dispatchEvent(ContentChanged(it?.toString())) }
  }

  private fun setTitleText(title: String?) {
    titleEditText.setText(title)
    titleEditText.requestFocus()
    titleEditText.setSelection(title?.length ?: 0)

    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(titleEditText, InputMethodManager.SHOW_IMPLICIT)
  }

  private fun setContentText(content: String?) {
    contentEditText.setText(content)
  }

  private fun closeEditor() {
    if (findNavController().currentDestination?.id == R.id.editorScreen) {
      val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
      imm?.hideSoftInputFromWindow(titleEditText.windowToken, 0)

      findNavController().navigateUp()
    }
  }

  override fun enableSave() {
    requireActivity().bottomBar.setContentActionEnabled(true)
  }

  override fun disableSave() {
    requireActivity().bottomBar.setContentActionEnabled(false)
  }

  override fun renderSaveActionButtonText() {
    requireActivity().bottomBar.setContentActionText(R.string.save)
  }

  override fun renderSaveAndPinActionButtonText() {
    requireActivity().bottomBar.setContentActionText(R.string.save_and_pin)
  }

  private fun showConfirmExitDialog() {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(getString(R.string.confirm_editor_exit_title))
      .setPositiveButton(R.string.confirm_editor_exit_positive_action) { _, _ ->
        viewModel.dispatchEvent(ConfirmedExit)
      }
      .setNegativeButton(R.string.confirm_editor_exit_negative_action) { _, _ ->
        // NO-OP
      }
      .show()
  }
}
