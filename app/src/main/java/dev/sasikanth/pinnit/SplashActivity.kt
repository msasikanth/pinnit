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
import dev.sasikanth.pinnit.databinding.ActivitySplashBinding

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

  private lateinit var binding: ActivitySplashBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySplashBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.rootView.setEdgeToEdgeSystemUiFlags()

    animatedWelcomeImage = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_pin_welcome)

    binding.welcomeImage.apply {
      setImageDrawable(animatedWelcomeImage)
      doOnLayout {
        animatedWelcomeImage?.start()
        animatedWelcomeImage?.registerAnimationCallback(animationCallback)
      }
    }
  }

  override fun onDestroy() {
    animatedWelcomeImage?.unregisterAnimationCallback(animationCallback)
    super.onDestroy()
  }
}
