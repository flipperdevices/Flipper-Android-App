object Versions {
  const val KOTLIN = "1.4.0"
  const val TIMBER = "4.7.1"

  const val ANDROID_GRADLE_PLUGIN = "4.0.1"
  const val ANDROID_APPCOMPAT = "1.2.0"
  const val ANDROID_CORE = "1.3.1"
  const val ANDROID_ANNOTATIONS = "1.1.0"

  const val ANDROIDX_TEST = "1.2.0"
  const val ANDROIDX_TEST_EXT = "1.1.1"
  const val ESPRESSO_CORE = "3.2.0"
  const val JUNIT = "4.12"
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
}

object TestingLib {
  const val JUNIT = "junit:junit:${Versions.JUNIT}"

  const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
  const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
  const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}
