plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.installation.stateprovider.api"

dependencies {
    implementation(projects.components.core.data)

    implementation(projects.components.faphub.dao.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.annotations)
}
