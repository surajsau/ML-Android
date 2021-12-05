package `in`.surajsau.jisho

object Dep {
    object Versions {
        object Compose {
            const val Core = "1.0.4"
            const val Foundation = "1.1.0-alpha-06"
            const val Navigation = "2.4.0-alpha10"
            const val RunTime = "1.1.0-alpha06"
            const val LifeCycle = "1.0.0-alpha07"
            const val Activity = "1.3.1"
        }

        const val Hilt = "2.38.1"
        const val HiltAndroid = "1.0.0-alpha03"

        const val Work = "2.7.0"
        const val Coil = "1.4.0"
        const val Accompanist = "0.21.4-beta"

        const val ExoPlayer = "2.15.1"

        const val Coroutines = "1.3.9"

        const val Retrofit = "2.9.0"
        const val Gson = "2.8.6"

        const val OkHttp = "4.9.1"

        const val Media3 = "1.0.0-alpha01"

        const val TensorFlow = "2.3.0"
        const val TensorFlowSupport = "0.1.0"

        const val Glide = "4.12.0"

        const val DigitalInk = "17.0.0"
        const val Translation = "16.1.2"

        const val CameraX = "1.0.1"
    }

    object Compose {
        const val Ui = "androidx.compose.ui:ui:${Versions.Compose.Core}"
        const val Runtime = "androidx.compose.runtime:runtime:${Versions.Compose.RunTime}"
        const val Material = "androidx.compose.material:material:${Versions.Compose.Core}"
        const val Tooling = "androidx.compose.ui:ui-tooling-preview:${Versions.Compose.Core}"
        const val Navigation = "androidx.navigation:navigation-compose:${Versions.Compose.Navigation}"
        const val Activity = "androidx.activity:activity-compose:${Versions.Compose.Activity}"

        const val ViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.Compose.LifeCycle}"
        const val LiveData = "androidx.compose.runtime:runtime-livedata:${Versions.Compose.RunTime}"

        const val Test = "androidx.compose.ui:ui-test-junit4:${Versions.Compose.Core}"
        const val ToolingTest = "androidx.compose.ui:ui-tooling:${Versions.Compose.Core}"
    }

    object AndroidX {
        const val Core = "androidx.core:core-ktx:1.6.0"
        const val AppCompat = "androidx.appcompat:appcompat:1.3.1"
        const val Material = "com.google.android.material:material:1.4.0"
        const val Lifeycycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
        const val Work = "androidx.work:work-runtime-ktx:${Versions.Work}"
    }

    object Hilt {
        const val Core = "com.google.dagger:hilt-android:${Versions.Hilt}"
        const val Compiler = "com.google.dagger:hilt-android-compiler:${Versions.Hilt}"
        const val Compose = "androidx.hilt:hilt-navigation-compose:${Versions.HiltAndroid}"
        const val ViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.HiltAndroid}"
        const val AndroidCompiler = "androidx.hilt:hilt-compiler:${Versions.HiltAndroid}"
    }

    object Coil {
        const val Compose = "io.coil-kt:coil-compose:${Versions.Coil}"
        const val Gif = "io.coil-kt:coil-gif:${Versions.Coil}"
        const val Video = "io.coil-kt:coil-video:${Versions.Coil}"
    }

    object Accompanist {
        const val SwipeRefresh = "com.google.accompanist:accompanist-swiperefresh:${Versions.Accompanist}"
        const val Permissions = "com.google.accompanist:accompanist-permissions:${Versions.Accompanist}"
    }

    object ExoPlayer {
        const val Core = "com.google.android.exoplayer:exoplayer:${Versions.ExoPlayer}"
        const val Ui = "com.google.android.exoplayer:exoplayer-ui:${Versions.ExoPlayer}"
    }

    object Media3 {
        const val ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.Media3}"
        const val Ui = "androidx.media3:media3-ui:${Versions.Media3}"
        const val Sessions = "androidx.media3:media3-session:${Versions.Media3}"
    }

    object Retrofit {
        const val Core = "com.squareup.retrofit2:retrofit:${Versions.Retrofit}"
        const val GsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.Retrofit}"
    }

    object OkHttp {
        const val LogginInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.OkHttp}"
    }

    object TensorFlow {
        const val Support = "org.tensorflow:tensorflow-lite-support:${Versions.TensorFlowSupport}"
        const val MetaData = "org.tensorflow:tensorflow-lite-metadata:${Versions.TensorFlowSupport}"
    }

    object Glide {
        const val Core = "com.github.bumptech.glide:glide:${Versions.Glide}"
        const val Compiler = "com.github.bumptech.glide:compiler:${Versions.Glide}"
    }

    object CameraX {
        const val Core = "androidx.camera:camera-camera2:${Versions.CameraX}"
        const val Lifecycle = "androidx.camera:camera-lifecycle:${Versions.CameraX}"
        const val View = "androidx.camera:camera-view:1.0.0-alpha27"
    }

    const val Gson = "com.google.code.gson:gson:${Versions.Gson}"

    const val Coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Coroutines}"

    object MLKit {
        const val Translation = "com.google.mlkit:translate:${Versions.Translation}"
        const val DigitalInk = "com.google.mlkit:digital-ink-recognition:${Versions.DigitalInk}"
    }
}

