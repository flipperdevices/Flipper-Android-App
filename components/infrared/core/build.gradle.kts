plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.infrared.core"

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.ktx)
}
