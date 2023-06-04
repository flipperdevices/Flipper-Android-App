plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("flipper.git-submodule-fetch")
}

android.namespace = "com.flipperdevices.nfc.tools.impl"

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
}
