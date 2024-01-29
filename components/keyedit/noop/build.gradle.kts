plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyedit.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.keyedit.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)

    implementation(libs.appcompat)
}
            }
        }
    }
