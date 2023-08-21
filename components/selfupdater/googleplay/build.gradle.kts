plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.selfupdater.source.googleplay"

dependencies {
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.activityholder)

    // In-app update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    implementation(libs.app.update.ktx)

    implementation(libs.kotlin.coroutines.play.services)

    // Dagger deps
    implementation(projects.components.core.di)
}
