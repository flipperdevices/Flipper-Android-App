plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.faphub.installation.queue.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
