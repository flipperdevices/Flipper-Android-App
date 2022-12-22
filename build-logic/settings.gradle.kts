enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://minecraft.glitchless.ru/repo/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://minecraft.glitchless.ru/repo/")
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"

include(
    ":plugins:convention"
)
