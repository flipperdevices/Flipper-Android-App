plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.res"

androidDependencies {
    implementation(libs.appcompat)
}

compose.resources {
    publicResClass = true
}
