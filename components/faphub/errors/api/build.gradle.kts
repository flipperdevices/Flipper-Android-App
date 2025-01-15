plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.errors"

commonDependencies {
    implementation(projects.components.core.ui.theme)

    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client)
    implementation(projects.components.bridge.rpc.api)
}
