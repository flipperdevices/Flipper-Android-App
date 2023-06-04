plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.selfupdater.source.googleplay"

dependencies {
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)

    // In-app update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // Dagger deps
    implementation(projects.components.core.di)
}
