plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.notification.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.permission.api)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.inappnotification.api)

    // Compose
    implementation(libs.lifecycle.compose)
    implementation(libs.decompose)

    implementation(libs.gms.firebase)
    implementation(libs.kotlin.coroutines.play.services)
    implementation(libs.kotlin.coroutines)

    implementation(libs.appcompat)
}
