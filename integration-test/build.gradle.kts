plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    api(Libs.CONDUCTOR)

    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.DAGGER)
    implementation(Libs.MOXY)
    implementation(Libs.MOXY_KTX)
    implementation(project(":core"))
}
