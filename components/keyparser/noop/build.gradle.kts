plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.parser.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.core.di)
}
            }
        }
    }
