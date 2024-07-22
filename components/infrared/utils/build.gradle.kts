plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.infrared.utils"

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.ktx)
}
