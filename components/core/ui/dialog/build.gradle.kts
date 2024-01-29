plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ui.dialog"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.components.core.ui.ktx)
            implementation(projects.components.core.ui.theme)
        }
    }
}