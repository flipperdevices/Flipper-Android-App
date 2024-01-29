plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.core.permission.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.permission.api)

    implementation(libs.dagger)
    implementation(libs.appcompat)

    implementation(libs.ktx.activity)
}
