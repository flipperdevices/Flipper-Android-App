plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.keyparser.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.compose.ui)
}
            }
        }
    }
