enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "pinnit"

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    google()
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
      repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
  }
}

include(":app")
include(":sharedTestCode")
