plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.faphub.installation.manifest.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
}
            }
        }
    }
