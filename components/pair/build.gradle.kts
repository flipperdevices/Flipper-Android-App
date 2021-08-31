plugins {
    id("com.android.library")
}

apply<com.flipper.gradle.ConfigurationPlugin>()
apply<com.flipper.gradle.ComposerPlugin>()

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:bridge"))

    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.DAGGER)
    implementation(Libs.MOXY)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)

    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.ROBOLECTRIC)
    testImplementation(TestingLib.ASSERTJ)
}
