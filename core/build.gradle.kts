plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

android {
  testOptions {
    unitTests.isIncludeAndroidResources = true
  }
}

dependencies {
    api(Libs.CONDUCTOR)

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
  testImplementation(project(":integration-test"))
}
