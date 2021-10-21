// NOTE_CONFIGURATION_PLUGIN
// when updating this dependency version, also update them in buildSrc/build.gradle.kts
// (manual update is required, because this file is not seen by build scripts in buildSrc)

object Versions {
    // Be careful! See more in /buildSrc/src/main/java/Dependencies.kt#NOTE_CONFIGURATION_PLUGIN
    const val KOTLIN = "1.5.21"

    const val KTLINT = "0.42.1"

    const val KOTLIN_COROUTINES = "1.5.2"

    const val ANDROIDX_APPCOMPAT = "1.3.1"
    const val ANDROIDX_CORE = "1.6.0"
    const val ANDROID_ANNOTATIONS = "1.2.0"
    const val ANDROID_MATERIAL = "1.2.0"
    const val ANDROID_JETPACK_COMPOSE = "1.0.2"
    const val ANDROID_LIFECYCLE = "2.3.1"

    const val FRAGMENT_KTX = "1.3.6"
    const val ACTIVITY_KTX = "1.3.1"

    const val ANDROIDX_TEST = "1.2.0"
    const val ANDROIDX_TEST_EXT = "1.1.1"
    const val ESPRESSO_CORE = "3.2.0"
    const val JUNIT = "4.12"

    const val TIMBER = "4.7.1"
    const val DAGGER = "2.38.1"
    const val CICERONE = "7.1"

    const val TREX = "1.0.0"
    const val FASTUTIL = "7.2.1"

    const val BLE_SCAN = "1.5.0"
    const val BLE = "2.3.0"

    const val IMAGE_SLIDER = "1.4.0"
    const val GLIDE = "4.12.0"
    const val LOTTIE = "4.2.0"

    const val PROTOBUF = "3.19.0"
}

object GradlePlugins {
    const val DETEKT = "1.18.1"
    const val KTLINT = "10.2.0"
}

object Libs {
    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"

    // KTX
    const val KOTLIN_COROUTINES =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KOTLIN_COROUTINES}"
    const val CORE_KTX = "androidx.core:core-ktx:${Versions.ANDROIDX_CORE}"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX}"
    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:${Versions.ACTIVITY_KTX}"

    // Lifecycle
    const val LIFECYCLE_VIEWMODEL_KTX =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.ANDROID_LIFECYCLE}"
    const val LIFECYCLE_RUNTIME_KTX =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.ANDROID_LIFECYCLE}"

    const val ANNOTATIONS = "androidx.annotation:annotation:${Versions.ANDROID_ANNOTATIONS}"
    const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.ANDROIDX_APPCOMPAT}"
    const val MATERIAL = "com.google.android.material:material:${Versions.ANDROID_MATERIAL}"

    const val COMPOSE_UI = "androidx.compose.ui:ui:${Versions.ANDROID_JETPACK_COMPOSE}"

    // Tooling support (Previews, etc.)
    const val COMPOSE_TOOLING = "androidx.compose.ui:ui-tooling:${Versions.ANDROID_JETPACK_COMPOSE}"

    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    const val COMPOSE_FOUNDATION =
        "androidx.compose.foundation:foundation:${Versions.ANDROID_JETPACK_COMPOSE}"

    // Material Design
    const val COMPOSE_MATERIAL =
        "androidx.compose.material:material:${Versions.ANDROID_JETPACK_COMPOSE}"

    const val CICERONE = "com.github.terrakok:cicerone:${Versions.CICERONE}"
    const val DAGGER = "com.google.dagger:dagger:${Versions.DAGGER}"
    const val DAGGER_COMPILER = "com.google.dagger:dagger-compiler:${Versions.DAGGER}"

    // Util
    const val TREX = "com.github.LionZXY.T-Rex-Android:trex-offline:${Versions.TREX}"
    const val FASTUTIL = "it.unimi.dsi:fastutil:${Versions.FASTUTIL}"

    // BLE
    const val NORDIC_BLE_SCAN = "no.nordicsemi.android.support.v18:scanner:${Versions.BLE_SCAN}"
    const val NORDIC_BLE = "no.nordicsemi.android:ble:${Versions.BLE}"
    const val NORDIC_BLE_KTX = "no.nordicsemi.android:ble-ktx:${Versions.BLE}"
    const val NORDIC_BLE_COMMON = "no.nordicsemi.android:ble-common:${Versions.BLE}"

    // Images
    const val IMAGE_SLIDER = "com.github.smarteist:Android-Image-Slider:${Versions.IMAGE_SLIDER}"
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val LOTTIE = "com.airbnb.android:lottie-compose:${Versions.LOTTIE}"

    // Protobuf
    const val PROTOBUF_JAVALITE = "com.google.protobuf:protobuf-javalite:${Versions.PROTOBUF}"
    const val PROTOBUF_GROUP = "com.google.protobuf"
    const val PROTOBUF_KOTLIN = "com.google.protobuf:protobuf-kotlin:${Versions.PROTOBUF}"
    const val PROTOBUF_PROTOC = "com.google.protobuf:protoc:${Versions.PROTOBUF}"
}

object TestingLib {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"

    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    const val ROBOLECTRIC = "org.robolectric:robolectric:4.6.1"
    const val ASSERTJ = "org.assertj:assertj-core:3.6.2"
}
