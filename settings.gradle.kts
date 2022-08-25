enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "FlipperApp"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":instances:app",
    ":components:bridge:api",
    ":components:bridge:impl",
    ":components:bridge:service:impl",
    ":components:bridge:service:api",
    ":components:bridge:pbutils",
    ":components:bridge:synchronization:api",
    ":components:bridge:synchronization:impl",
    ":components:bridge:synchronization:stub",
    ":components:bridge:synchronization:ui",
    ":components:bridge:dao:api",
    ":components:bridge:dao:impl",

    ":components:filemanager:api",
    ":components:filemanager:impl",

    ":components:core:di",
    ":components:core:ktx",
    ":components:core:log",
    ":components:core:navigation",
    ":components:core:preference",
    ":components:core:ui:ktx",
    ":components:core:ui:res",
    ":components:core:ui:dialog",
    ":components:core:ui:lifecycle",
    ":components:core:ui:fragment",
    ":components:core:ui:theme",
    ":components:core:ui:hexkeyboard",
    ":components:core:ui:navigation",
    ":components:core:test",
    ":components:core:markdown",
    ":components:core:activityholder",

    ":components:bottombar:api",
    ":components:bottombar:impl",

    ":components:info:api",
    ":components:info:impl",
    ":components:info:shared",

    ":components:analytics:metric:api",
    ":components:analytics:metric:impl",

    ":components:analytics:shake2report:api",
    ":components:analytics:shake2report:noop",
    ":components:analytics:shake2report:impl",

    ":components:screenstreaming:impl",
    ":components:screenstreaming:api",

    ":components:share:api",
    ":components:share:receive",

    ":components:singleactivity:api",
    ":components:singleactivity:impl",

    ":components:deeplink:api",
    ":components:deeplink:impl",

    ":components:debug:api",
    ":components:debug:stresstest",

    ":components:archive:api",
    ":components:archive:impl",
    ":components:archive:category",
    ":components:archive:search",
    ":components:archive:shared",

    ":components:connection:api",
    ":components:connection:impl",

    ":components:keyscreen:api",
    ":components:keyscreen:impl",
    ":components:keyscreen:shared",

    ":components:keyedit:api",
    ":components:keyedit:impl",

    ":components:firstpair:api",
    ":components:firstpair:impl",

    ":components:inappnotification:api",
    ":components:inappnotification:impl",

    ":components:settings:api",
    ":components:settings:impl",

    ":components:updater:api",
    ":components:updater:impl",
    ":components:updater:screen",
    ":components:updater:downloader",
    ":components:updater:card",

    ":components:nfceditor:api",
    ":components:nfceditor:impl",
    ":components:nfceditor:sample"
)
