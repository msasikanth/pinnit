package dev.sasikanth.pinnit.editor

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
sealed class EditorTransition : Parcelable {

  @Parcelize
  data object SharedAxis : EditorTransition()

  @Parcelize
  data class ContainerTransform(val transitionName: String) : EditorTransition()
}
