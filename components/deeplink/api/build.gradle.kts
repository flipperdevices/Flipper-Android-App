plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.deeplink.api"

commonDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.kmpparcelize)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}
