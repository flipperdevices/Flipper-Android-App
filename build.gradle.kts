plugins {
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.square.anvil) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.google.gms) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
<<<<<<< feature/k2
    id("flipper.lint")
    alias(libs.plugins.compose) apply false
=======
    alias(libs.plugins.buildkonfig) apply false
>>>>>>> dev
}
