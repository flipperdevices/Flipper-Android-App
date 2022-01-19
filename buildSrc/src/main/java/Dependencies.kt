// NOTE_CONFIGURATION_PLUGIN
// when updating this dependency version, also update them in buildSrc/build.gradle.kts
// (manual update is required, because this file is not seen by build scripts in buildSrc)

object GradlePlugins {
    const val DETEKT = "1.19.0"
    const val KTLINT = "10.2.0"
}
