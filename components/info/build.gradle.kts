plugins {
    id("com.android.library")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply<com.flipper.gradle.ConfigurationPlugin>()
apply<com.flipper.gradle.ComposerPlugin>()

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // Core deps
    implementation(Libs.CORE_KTX)
    implementation(Libs.ANNOTATIONS)

    implementation(Libs.APPCOMPAT)
    implementation(Libs.DAGGER)

    implementation(project(":components:core"))
    implementation(project(":components:bridge"))

    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_MATERIAL)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)

    implementation(Libs.FRAGMENT_KTX)

    kapt(Libs.DAGGER_COMPILER)
}
