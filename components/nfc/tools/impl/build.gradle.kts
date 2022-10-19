plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android {
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
            ndkVersion = "25.1.8937393"
        }
    }
}

dependencies {
    implementation(projects.components.nfc.tools.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}