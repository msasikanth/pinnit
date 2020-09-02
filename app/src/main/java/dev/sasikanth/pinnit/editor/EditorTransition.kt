package dev.sasikanth.pinnit.editor

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
sealed class EditorTransition : Parcelable {

  @Parcelize
  object SharedAxis : EditorTransition()

  @Parcelize
  data class ContainerTransform(val transitionName: String) : EditorTransition()
}
