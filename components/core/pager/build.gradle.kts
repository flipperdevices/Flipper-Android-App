plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.pager"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.core.log)

            implementation(libs.compose.paging)
        }
    }
}
