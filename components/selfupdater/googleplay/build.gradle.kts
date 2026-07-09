plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
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
