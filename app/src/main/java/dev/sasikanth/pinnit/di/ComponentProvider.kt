package dev.sasikanth.pinnit.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface ComponentProvider {
  val component: AppComponent
}

val FragmentActivity.injector get() = (application as ComponentProvider).component
val Fragment.injector get() = (requireActivity().application as ComponentProvider).component
