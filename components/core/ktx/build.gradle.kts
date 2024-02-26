plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ktx"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.components.core.log)
            implementation(libs.kotlin.coroutines)
        }
        androidMain.dependencies {
            implementation(libs.appcompat)
        }
        commonTest.dependencies {
            implementation(libs.junit)
        }
    }
}
