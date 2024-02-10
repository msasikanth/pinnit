plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.android.library).apply(false)
  alias(libs.plugins.kotlin.android).apply(false)
  alias(libs.plugins.kotlin.parcelize).apply(false)
  alias(libs.plugins.androidx.navigation).apply(false)
  alias(libs.plugins.hilt).apply(false)
  alias(libs.plugins.protobuf).apply(false)
  alias(libs.plugins.ksp).apply(false)
}
