plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.faphub.category.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}
            }
        }
    }
