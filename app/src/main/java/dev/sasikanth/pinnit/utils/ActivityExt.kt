package dev.sasikanth.pinnit.utils

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotify.mobius.Connectable
import com.spotify.mobius.Init
import com.spotify.mobius.Mobius
import com.spotify.mobius.Update
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Consumer

fun <M : Any, E : Any, F : Any, V : Any> AppCompatActivity.pinnitViewModels(
  defaultProducer: () -> M,
  initProducer: () -> Init<M, F>,
  updateProducer: () -> Update<M, E, F>,
  effectHandlerProducer: (Consumer<V>) -> Connectable<F, E>,
): Lazy<MobiusLoopViewModel<M, E, F, V>> = viewModels {
  object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return MobiusLoopViewModel.create<M, E, F, V>(
        { viewEffectConsumer ->
          Mobius.loop(
            updateProducer.invoke(),
            effectHandlerProducer.invoke(viewEffectConsumer)
          )
        },
        defaultProducer.invoke(),
        initProducer.invoke()
      ) as T
    }
  }
}
