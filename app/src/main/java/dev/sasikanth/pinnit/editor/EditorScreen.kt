package dev.sasikanth.pinnit.editor

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Function
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notification_editor.*
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
        val notificationUuid = args.notification?.uuid

        return MobiusLoopViewModel.create<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect>(
          Function { viewEffectConsumer ->
            Mobius.loop(
              EditorScreenUpdate(),
              effectHandler.create(viewEffectConsumer)
            )
          },
          EditorScreenModel.default(uuid = notificationUuid),
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
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel.models.observe(viewLifecycleOwner, Observer { model ->
      uiRender.render(model)
    })

    viewModel.viewEffects.setObserver(viewLifecycleOwner, Observer {
      when (it) {
        is CloseEditor -> {
          if (it.notification.isPinned) {
            NotificationUtil.showNotification(requireContext(), it.notification)
          }
          closeEditor()
        }

        is SetTitle -> titleEditText.setText(it.title)

        is SetContent -> contentEditText.setText(it.content)
      }
    })

    requireActivity().bottomBar.setNavigationIcon(R.drawable.ic_arrow_back)
    requireActivity().bottomBar.setContentActionEnabled(false)
    if (args.notification?.isPinned == true) {
      requireActivity().bottomBar.setContentActionText(R.string.save)
    } else {
      requireActivity().bottomBar.setContentActionText(R.string.save_and_pin)
    }
    requireActivity().bottomBar.setActionIcon(null)

    requireActivity().bottomBar.setNavigationOnClickListener {
      closeEditor()
    }
    requireActivity().bottomBar.setContentActionOnClickListener {
      viewModel.dispatchEvent(SaveClicked)
    }

    titleEditTextConfig()
    contentEditTextConfig()
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

  private fun titleEditTextConfig() {
    titleEditText.requestFocus()
    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(titleEditText, InputMethodManager.SHOW_IMPLICIT)

    titleEditText.doAfterTextChanged { viewModel.dispatchEvent(TitleChanged(it?.toString().orEmpty())) }
  }

  private fun contentEditTextConfig() {
    contentEditText.doAfterTextChanged { viewModel.dispatchEvent(ContentChanged(it?.toString())) }
  }
}
