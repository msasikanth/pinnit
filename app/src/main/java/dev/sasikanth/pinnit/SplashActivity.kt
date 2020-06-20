package dev.sasikanth.pinnit

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags
import dev.sasikanth.pinnit.activity.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

  private val animationCallback = object : Animatable2Compat.AnimationCallback() {
    override fun onAnimationEnd(drawable: Drawable?) {
      super.onAnimationEnd(drawable)
      val intent = Intent(this@SplashActivity, MainActivity::class.java)
      startActivity(intent)
      finish()
    }
  }

  private var animatedWelcomeImage: AnimatedVectorDrawableCompat? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    rootView.setEdgeToEdgeSystemUiFlags()

    animatedWelcomeImage = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_pin_welcome)

    welcomeImage.setImageDrawable(animatedWelcomeImage)
    welcomeImage.doOnLayout {
      animatedWelcomeImage?.start()
      animatedWelcomeImage?.registerAnimationCallback(animationCallback)
    }
  }

  override fun onDestroy() {
    animatedWelcomeImage?.unregisterAnimationCallback(animationCallback)
    super.onDestroy()
  }
}