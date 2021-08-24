plugins {
    id("com.android.library")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.DAGGER)
    implementation(Libs.MOXY)

    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.ROBOLECTRIC)
    testImplementation(TestingLib.ASSERTJ)
}
