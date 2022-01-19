// NOTE_CONFIGURATION_PLUGIN
// when updating this dependency version, also update them in buildSrc/build.gradle.kts
// (manual update is required, because this file is not seen by build scripts in buildSrc)

object Versions {
    const val KTLINT = "0.44.0-SNAPSHOT"

    // Test
    const val ANDROIDX_TEST = "1.2.0"
    const val ANDROIDX_TEST_EXT = "1.1.3"
    const val JUNIT = "4.12"
    const val MOCKITO_KOTLIN = "4.0.0"
    const val ROBOELECTRIC = "4.6.1"
}

object GradlePlugins {
    const val DETEKT = "1.19.0"
    const val KTLINT = "10.2.0"
}

object TestingLib {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"

    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val MOCKITO = "org.mockito.kotlin:mockito-kotlin:${Versions.MOCKITO_KOTLIN}"
    const val ROBOELECTRIC = "org.robolectric:robolectric:${Versions.ROBOELECTRIC}"
}
