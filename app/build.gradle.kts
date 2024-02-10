import com.google.protobuf.gradle.GenerateProtoTask
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.androidx.navigation)
  alias(libs.plugins.hilt)
  alias(libs.plugins.protobuf)
  alias(libs.plugins.ksp)
}

android {
  signingConfigs {
    create("release") {
      storeFile = file("$rootDir/release/app-release.jks")
      storePassword = "${project.properties["PINNIT_KEYSTORE_PASSWORD"]}"
      keyAlias = "pinnitalias"
      keyPassword = "${project.properties["PINNIT_KEY_PASSWORD"]}"
    }
  }

  namespace = "dev.sasikanth.pinnit"

  compileSdk = libs.versions.sdk.compile.get().toInt()

  defaultConfig {
    applicationId = "dev.sasikanth.pinnit"
    minSdk = libs.versions.sdk.min.get().toInt()
    targetSdk = libs.versions.sdk.target.get().toInt()

    versionCode = if (project.properties["VERSION_CODE"] != null) {
      (project.properties["VERSION_CODE"] as String).toInt()
    } else {
      1
    }

    versionName = if (project.properties["VERSION_NAME"] != null) {
      project.properties["VERSION_NAME"] as String
    } else {
      "1.0.0"
    }

    testInstrumentationRunner = "dev.sasikanth.pinnit.AndroidTestRunner"

    vectorDrawables.useSupportLibrary = true

    ksp {
      arg("room.schemaLocation", "$projectDir/schemas".toString())
      arg("room.incremental", "true")
    }
  }

  sourceSets {
    getByName("androidTest").assets.srcDirs(
      files("$projectDir/schemas")
    )

    val sharedTestDir = "src/sharedTest/java"
    getByName("test") {
      java.srcDir(sharedTestDir)
    }
    getByName("androidTest") {
      java.srcDir(sharedTestDir)
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      applicationIdSuffix = ".debug"
    }
  }
  buildFeatures {
    viewBinding = true
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()

    // Enabling experimental coroutines APIs
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-opt-in=kotlin.time.ExperimentalTime"
    )
  }
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${libs.versions.protoc.get()}"
  }

  // Generates the java Protobuf-lite code for the Protobufs in this project. See
  // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
  // for more information.
  generateProtoTasks {
    all().forEach { task ->
      task.builtins {
        create("java") {
          option("lite")
        }
      }
    }
  }
}

androidComponents {
  onVariants(selector().all()) { variant ->
    afterEvaluate {
      val variantName = variant.name.uppercaseFirstChar()
      val protoTask =
        project.tasks.getByName("generate${variantName}Proto") as GenerateProtoTask

      project.tasks.getByName("ksp${variantName}Kotlin") {
        dependsOn(protoTask)
        (this as org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool<*>).setSource(
          protoTask.outputBaseDir
        )
      }
    }
  }
}

dependencies {
  coreLibraryDesugaring(libs.desugar.jdk)
  implementation(libs.kotlin.stdlib)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.corektx)
  implementation(libs.androidx.fragmentktx)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.bundles.androidx.navigation)
  implementation(libs.bundles.androidx.room)
  ksp(libs.androidx.room.compiler)
  implementation(libs.bundles.androidx.lifecycle)
  implementation(libs.androidx.work)
  implementation(libs.material)
  debugImplementation(libs.leakcanary)
  implementation(libs.bundles.coroutines)
  implementation(libs.hilt.android)
  implementation(libs.hilt.work)
  ksp(libs.hilt.android.compiler)
  ksp(libs.hilt.compiler)
  implementation(libs.bundles.mobius)
  implementation(libs.circleImageView)
  implementation(libs.insetterktx)
  implementation(libs.betterLinkMovement)
  implementation(libs.androidx.seekableVD)
  implementation(libs.androidx.datastore)
  implementation(libs.javalite)
  implementation(libs.flowBinding)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(libs.androidx.room.testing)
  testImplementation(libs.androidx.core.testing)
  testImplementation(libs.mockito)
  testImplementation(libs.kotlin.coroutines.test)
  testImplementation(libs.mobius.test)
  testImplementation(libs.androidx.datastore)
  testImplementation(projects.sharedTestCode)

  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.espresso)
  androidTestImplementation(libs.truth)
  androidTestImplementation(libs.androidx.work.testing)
  androidTestImplementation(libs.hilt.testing)
  kspAndroidTest(libs.hilt.android.compiler)
  androidTestImplementation(libs.androidx.datastore)
  androidTestImplementation(projects.sharedTestCode)
}
