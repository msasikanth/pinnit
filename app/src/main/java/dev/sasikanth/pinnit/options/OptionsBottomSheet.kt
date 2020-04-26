package dev.sasikanth.pinnit.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sasikanth.pinnit.R

class OptionsBottomSheet : BottomSheetDialogFragment() {

  companion object {
    const val TAG = "OptionsBottomSheet"
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.theme_selection_sheet, container, false)
  }
}
