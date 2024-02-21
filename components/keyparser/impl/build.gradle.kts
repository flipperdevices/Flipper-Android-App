plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyparser.impl"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.keyparser.api)
            implementation(projects.components.bridge.dao.api)

            implementation(projects.components.core.di)
            implementation(projects.components.core.log)
            implementation(projects.components.core.data)

            implementation(libs.kotlin.immutable.collections)
            implementation(libs.kotlin.coroutines)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockk)
            implementation(libs.ktx.testing)
            implementation(libs.roboelectric)
            implementation(libs.lifecycle.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}
