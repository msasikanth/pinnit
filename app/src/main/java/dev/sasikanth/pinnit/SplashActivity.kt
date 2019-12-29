package dev.sasikanth.pinnit

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import dev.sasikanth.pinnit.shared.fullScreen
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

  private val animationCallback = object : Animatable2.AnimationCallback() {
    override fun onAnimationEnd(drawable: Drawable?) {
      super.onAnimationEnd(drawable)
      val intent = Intent(this@SplashActivity, MainActivity::class.java)
      startActivity(intent)
      finish()
    }
  }

  private lateinit var animatedWelcomeImage: AnimatedVectorDrawable

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    rootView.fullScreen()
    animatedWelcomeImage = welcomeImage.drawable as AnimatedVectorDrawable
    welcomeImage.doOnLayout {
      animatedWelcomeImage.start()
      animatedWelcomeImage.registerAnimationCallback(animationCallback)
    }
  }

  override fun onDestroy() {
    animatedWelcomeImage.unregisterAnimationCallback(animationCallback)
    super.onDestroy()
  }
}