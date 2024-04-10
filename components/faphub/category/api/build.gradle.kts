plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.category.api"

dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}
