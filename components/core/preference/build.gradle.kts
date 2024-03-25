plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.core.preference"

dependencies {
    implementation(projects.components.core.di)

    api(libs.datastore)
}
