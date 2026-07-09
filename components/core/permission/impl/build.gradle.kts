plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.core.permission.impl"

androidDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.permission.api)
    implementation(libs.appcompat)

    implementation(libs.ktx.activity)
}
