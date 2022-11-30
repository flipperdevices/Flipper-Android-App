enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
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
    ":instances:wearable",
    ":components:bridge:api",
    ":components:bridge:impl",
    ":components:bridge:service:noop",
    ":components:bridge:service:impl",
    ":components:bridge:service:api",
    ":components:bridge:pbutils",
    ":components:bridge:synchronization:api",
    ":components:bridge:synchronization:impl",
    ":components:bridge:synchronization:stub",
    ":components:bridge:synchronization:ui",
    ":components:bridge:dao:api",
    ":components:bridge:dao:impl",
    ":components:bridge:dao:noop",

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
    ":components:core:ui:searchbar",
    ":components:core:ui:hexkeyboard",
    ":components:core:ui:navigation",
    ":components:core:test",
    ":components:core:markdown",
    ":components:core:activityholder",
    ":components:core:share",

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
    ":components:share:uploader",
    ":components:share:cryptostorage",

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
    ":components:keyscreen:emulate",
    ":components:keyscreen:shared",

    ":components:keyedit:api",
    ":components:keyedit:impl",
    ":components:keyedit:noop",

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
    ":components:updater:subghz",

    ":components:nfceditor:api",
    ":components:nfceditor:impl",
    ":components:nfceditor:sample",

    ":components:wearable:theme",
    ":components:wearable:core:ui:components",
    ":components:wearable:core:ui:ktx",
    ":components:wearable:setup:api",
    ":components:wearable:setup:impl",

    ":components:wearable:sync:common",
    ":components:wearable:sync:wear:api",
    ":components:wearable:sync:wear:impl",
    ":components:wearable:sync:handheld:api",
    ":components:wearable:sync:handheld:impl",
    ":components:wearable:sync:handheld:noop",
    ":components:wearable:emulate:wear:api",
    ":components:wearable:emulate:wear:impl",
    ":components:wearable:emulate:handheld:api",
    ":components:wearable:emulate:handheld:impl",
    ":components:wearable:emulate:handheld:noop",
    ":components:wearable:emulate:common",

    ":components:widget:api",
    ":components:widget:screen",
    ":components:widget:impl",

    ":components:nfc:mfkey32:api",
    ":components:nfc:mfkey32:screen",
    ":components:nfc:tools:api",
    ":components:nfc:tools:impl",
    ":components:nfc:attack:api",
    ":components:nfc:attack:impl",

    ":components:hub:api",
    ":components:hub:impl",

    ":components:faphub:maincard:api",
    ":components:faphub:maincard:impl",
    ":components:faphub:appcard:api",
    ":components:faphub:appcard:composable",
    ":components:faphub:dao:api",
    ":components:faphub:dao:network",
    ":components:faphub:dao:flipper",
    ":components:faphub:main:api",
    ":components:faphub:main:impl",
    ":components:faphub:catalogtab:api",
    ":components:faphub:catalogtab:impl",
    ":components:faphub:search:api",
    ":components:faphub:search:impl",
)