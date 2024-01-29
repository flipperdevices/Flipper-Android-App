plugins {
    id("flipper.multiplatform-compose")
    id("kotlin-parcelize")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.wearable.sync.common"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)

    implementation(libs.compose.ui)
}
            }
        }
    }
