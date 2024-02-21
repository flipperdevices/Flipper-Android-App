plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.share.cryptostorage"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.share.api)

            implementation(projects.components.core.di)
            implementation(projects.components.core.log)
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.preference)
            implementation(projects.components.core.ui.ktx)
            implementation(projects.components.core.ui.res)

            implementation(projects.components.core.ui.theme)
            implementation(projects.components.bridge.dao.api)
            implementation(projects.components.keyparser.api)

            implementation(libs.appcompat)

            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.serialization.json)

            implementation(libs.ktor.client)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.negotiation)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.roboelectric)
            implementation(libs.ktx.testing)
            implementation(libs.mockk)
        }
    }
}
