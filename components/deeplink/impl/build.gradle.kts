plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.deeplink.impl"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.filemanager.api)
    implementation(projects.components.share.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
