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
    id("flipper.lint")
    id("flipper.android-app") apply false
    id("flipper.android-compose") apply false
    id("flipper.android-lib") apply false
    id("flipper.anvil") apply false
    id("flipper.anvil.kapt") apply false
    id("flipper.multiplatform") apply false
    id("flipper.multiplatform-compose") apply false
}
