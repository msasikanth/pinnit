package dev.sasikanth.pinnit.di

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment

interface ComponentProvider {
  val component: PinnitAppComponent
}

val Activity.injector get() = (application as ComponentProvider).component
val Fragment.injector get() = (requireActivity().application as ComponentProvider).component
val Service.injector get() = (application as ComponentProvider).component
