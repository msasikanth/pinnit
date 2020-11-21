package dev.sasikanth.pinnit.utils

import android.os.CountDownTimer
import androidx.vectordrawable.graphics.drawable.SeekableAnimatedVectorDrawable

fun SeekableAnimatedVectorDrawable.reverse() {
  object : CountDownTimer(totalDuration, 1) {
    override fun onTick(interval: Long) {
      currentPlayTime = interval
    }

    override fun onFinish() {
      currentPlayTime = 0
    }
  }.start()
}
