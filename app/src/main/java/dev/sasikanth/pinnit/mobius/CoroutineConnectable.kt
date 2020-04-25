package dev.sasikanth.pinnit.mobius

import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class CoroutineConnectable<I, O>(
  private val dispatcher: CoroutineDispatcher
) : Connectable<I, O>, CoroutineScope {

  private val parentJob = SupervisorJob()
  override val coroutineContext: CoroutineContext
    get() = parentJob + dispatcher

  abstract suspend fun handler(effect: I, dispatchEvent: (O) -> Unit)

  override fun connect(output: Consumer<O>): Connection<I> {
    val job = Job()
    val scope = CoroutineScope(coroutineContext + job)

    return object : Connection<I> {
      override fun accept(input: I) {
        scope.launch { handler(input, output::accept) }
      }

      override fun dispose() {
        job.cancel()
        parentJob.cancel()
      }
    }
  }
}
