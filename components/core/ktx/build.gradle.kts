plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ktx"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.log)

    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)

    implementation(libs.kotlin.coroutines)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
}
            }
        }
    }
