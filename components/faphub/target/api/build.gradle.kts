plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.faphub.target.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
