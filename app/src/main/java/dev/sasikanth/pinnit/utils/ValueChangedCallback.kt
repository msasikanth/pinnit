package dev.sasikanth.pinnit.utils

// Source: https://github.com/simpledotorg/simple-android/blob/4f317057b2b21732f835df5c3c6c524ad16c9a77/app/src/main/java/org/simple/clinic/util/ValueChangedCallback.kt
class ValueChangedCallback<T> {
  private var cachedValue: T? = null

  fun pass(newValue: T, callback: (T) -> Unit) {
    if (cachedValue != newValue) {
      callback(newValue)
      cachedValue = newValue
    }
  }
}
