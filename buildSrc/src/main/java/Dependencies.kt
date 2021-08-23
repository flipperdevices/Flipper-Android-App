object Versions {
    // SEE NOTE_CONFIGURATION_PLUGIN
    const val KOTLIN = "1.5.21"
    const val TIMBER = "4.7.1"

    // SEE NOTE_CONFIGURATION_PLUGIN
    const val ANDROID_GRADLE_PLUGIN = "7.0.1"
    const val ANDROID_APPCOMPAT = "1.2.0"
    const val ANDROID_CORE = "1.3.1"
    const val ANDROID_ANNOTATIONS = "1.1.0"
    const val ANDROID_MATERIAL = "1.2.0"

    const val ANDROIDX_TEST = "1.2.0"
    const val ANDROIDX_TEST_EXT = "1.1.1"
    const val ESPRESSO_CORE = "3.2.0"
    const val JUNIT = "4.12"

    const val MOXY = "2.2.2"
    const val CONDUCTOR = "3.0.0-rc5"
    const val DAGGER = "2.38.1"
    const val TREX = "1.0.0"
}

object GradlePlugins {
    const val ANDROID_GRADLE_PLUGIN =
        "com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN}"
    const val KOTLIN_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
}

object Libs {
    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"

    const val ANNOTATIONS = "androidx.annotation:annotation:${Versions.ANDROID_ANNOTATIONS}"
    const val CORE_KTX = "androidx.core:core-ktx:${Versions.ANDROID_CORE}"
    const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.ANDROID_APPCOMPAT}"
    const val MATERIAL = "com.google.android.material:material:${Versions.ANDROID_MATERIAL}"

    const val MOXY = "com.github.moxy-community:moxy:${Versions.MOXY}"
    const val MOXY_KTX = "com.github.moxy-community:moxy-ktx:${Versions.MOXY}"
    const val MOXY_COMPILER = "com.github.moxy-community:moxy-compiler:${Versions.MOXY}"
    const val CONDUCTOR = "com.bluelinelabs:conductor:${Versions.CONDUCTOR}"

    const val DAGGER = "com.google.dagger:dagger:${Versions.DAGGER}"
    const val DAGGER_COMPILER = "com.google.dagger:dagger-compiler:${Versions.DAGGER}"

    const val TREX = "com.github.LionZXY.T-Rex-Android:trex-offline:${Versions.TREX}"
}

object TestingLib {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"

    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    const val ROBOLECTRIC = "org.robolectric:robolectric:4.6.1"
    const val ASSERTJ = "org.assertj:assertj-core:3.6.2"
}
