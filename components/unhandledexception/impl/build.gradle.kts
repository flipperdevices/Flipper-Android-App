plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.unhandledexception.impl"

androidDependencies {
    implementation(projects.components.unhandledexception.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)

    implementation(projects.components.rootscreen.api)
    implementation(projects.components.deeplink.api)
}
