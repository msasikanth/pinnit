package dev.sasikanth.pinnit.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.spotify.mobius.Connectable
import com.spotify.mobius.Init
import com.spotify.mobius.Mobius
import com.spotify.mobius.Update
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.functions.Consumer

fun <M : Any, E : Any, F : Any, V : Any> Fragment.pinnitViewModels(
  defaultProducer: () -> M,
  initProducer: () -> Init<M, F>,
  updateProducer: () -> Update<M, E, F>,
  effectHandlerProducer: (Consumer<V>) -> Connectable<F, E>,
  ownerProducer: () -> ViewModelStoreOwner = { this },
): Lazy<MobiusLoopViewModel<M, E, F, V>> = viewModels(ownerProducer) {
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
