plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.wire")
}

android.namespace = "com.flipperdevices.wearable.sync.common"

androidDependencies {
    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)
}
