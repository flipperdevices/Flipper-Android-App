plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
    id("com.google.devtools.ksp")
}

android.namespace = "com.flipperdevices.bridge.dao.impl"

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.bridge.dao.api)

            implementation(projects.components.core.di)
            implementation(projects.components.core.log)
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.preference)

            implementation(libs.room.runtime)
            implementation(libs.room.ktx)

            implementation(libs.kotlin.immutable.collections)
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockito.kotlin)
            implementation(libs.ktx.testing)
            implementation(libs.roboelectric)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}
dependencies { kspAndroid(libs.room.ksp) }
