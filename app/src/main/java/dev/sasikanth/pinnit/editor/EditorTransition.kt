package dev.sasikanth.pinnit.editor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class EditorTransition : Parcelable {

  @Parcelize
  object SharedAxis : EditorTransition()

  @Parcelize
  data class ContainerTransform(val transitionName: String) : EditorTransition()
}
