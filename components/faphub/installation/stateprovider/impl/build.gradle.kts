plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.installation.stateprovider.impl"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.faphub.installation.stateprovider.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.api)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.target.api)

    implementation(projects.components.bridge.rpc.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.coroutines.test)
}
            }
        }
    }
