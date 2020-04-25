package dev.sasikanth.pinnit.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

class TestDispatcherProvider : DispatcherProvider {

  private val testCoroutineDispatcher = TestCoroutineDispatcher()

  override val main: CoroutineDispatcher
    get() = testCoroutineDispatcher

  override val io: CoroutineDispatcher
    get() = testCoroutineDispatcher

  override val default: CoroutineDispatcher
    get() = testCoroutineDispatcher
}
