plugins {
    id("flipper.multiplatform")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.dao.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.data)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.preference)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    implementation(projects.components.faphub.target.api)

    implementation(libs.annotations)
    implementation(libs.compose.ui)
}
            }
        }
    }
