plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.selfupdater.unknown"

dependencies {
    implementation(projects.components.selfupdater.api)

    implementation(projects.components.core.di)
}
