plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.utils"

dependencies {
    implementation(projects.components.core.data)
    
    implementation(libs.kotlin.coroutines)

    implementation(libs.dagger)
}
