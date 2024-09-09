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
    ":instances:android:app",
    ":instances:wearable",
    ":components:bridge:api",
    ":components:bridge:impl",
    ":components:bridge:service:noop",
    ":components:bridge:service:impl",
    ":components:bridge:service:api",
    ":components:bridge:rpcinfo:api",
    ":components:bridge:rpcinfo:impl",
    ":components:bridge:rpc:api",
    ":components:bridge:rpc:impl",
    ":components:bridge:pbutils",
    ":components:bridge:synchronization:api",
    ":components:bridge:synchronization:impl",
    ":components:bridge:synchronization:stub",
    ":components:bridge:synchronization:ui",
    ":components:bridge:dao:api",
    ":components:bridge:dao:impl",

    ":components:bridge:connection:pbutils",
    ":components:bridge:connection:transport:ble:api",
    ":components:bridge:connection:transport:ble:impl",
    ":components:bridge:connection:transport:common:api",
    ":components:bridge:connection:transport:common:impl",
    ":components:bridge:connection:orchestrator:api",
    ":components:bridge:connection:orchestrator:impl",
    ":components:bridge:connection:connectionbuilder:api",
    ":components:bridge:connection:connectionbuilder:impl",
    ":components:bridge:connection:sample",
    ":components:bridge:connection:config:api",
    ":components:bridge:connection:config:impl",
    ":components:bridge:connection:transportconfigbuilder:api",
    ":components:bridge:connection:transportconfigbuilder:impl",
    ":components:bridge:connection:device:common:api",
    ":components:bridge:connection:device:fzero:api",
    ":components:bridge:connection:device:fzero:impl",
    ":components:bridge:connection:feature:common:api",
    ":components:bridge:connection:feature:provider:api",
    ":components:bridge:connection:feature:provider:impl",
    ":components:bridge:connection:feature:rpc:api",
    ":components:bridge:connection:feature:rpc:impl",
    ":components:bridge:connection:feature:rpc:model",
    ":components:bridge:connection:feature:rpcinfo:api",
    ":components:bridge:connection:feature:rpcinfo:impl",
    ":components:bridge:connection:feature:rpcstats:api",
    ":components:bridge:connection:feature:rpcstats:impl",
    ":components:bridge:connection:feature:storageinfo:api",
    ":components:bridge:connection:feature:storageinfo:impl",
    ":components:bridge:connection:feature:getinfo:api",
    ":components:bridge:connection:feature:getinfo:impl",
    ":components:bridge:connection:feature:restartrpc:api",
    ":components:bridge:connection:feature:restartrpc:impl",
    ":components:bridge:connection:feature:protocolversion:api",
    ":components:bridge:connection:feature:protocolversion:impl",
    ":components:bridge:connection:feature:lagsdetector:api",
    ":components:bridge:connection:feature:lagsdetector:impl",
    ":components:bridge:connection:feature:serialspeed:api",
    ":components:bridge:connection:feature:serialspeed:impl",

    ":components:filemanager:api",
    ":components:filemanager:impl",

    ":components:core:di",
    ":components:core:ktx",
    ":components:core:log",
    ":components:core:preference",
    ":components:core:data",
    ":components:core:build-konfig",
    ":components:core:ui:ktx",
    ":components:core:ui:res",
    ":components:core:ui:dialog",
    ":components:core:ui:lifecycle",
    ":components:core:ui:theme",
    ":components:core:ui:searchbar",
    ":components:core:ui:hexkeyboard",
    ":components:core:ui:tabswitch",
    ":components:core:ui:flippermockup",
    ":components:core:ui:scrollbar",
    ":components:core:ui:decompose",
    ":components:core:test",
    ":components:core:markdown",
    ":components:core:activityholder",
    ":components:core:share",
    ":components:core:pager",
    ":components:core:progress",
    ":components:core:permission:api",
    ":components:core:permission:impl",
    ":components:core:kmpparcelize",

    ":components:bottombar:api",
    ":components:bottombar:impl",

    ":components:info:api",
    ":components:info:impl",
    ":components:info:shared",

    ":components:analytics:metric:api",
    ":components:analytics:metric:impl",
    ":components:analytics:metric:noop",

    ":components:analytics:shake2report:api",
    ":components:analytics:shake2report:noop",
    ":components:analytics:shake2report:impl",

    ":components:screenstreaming:impl",
    ":components:screenstreaming:api",
    ":components:screenstreaming:noop",

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
    ":components:keyscreen:shared",

    ":components:keyemulate:api",
    ":components:keyemulate:impl",

    ":components:keyparser:api",
    ":components:keyparser:impl",
    ":components:keyparser:noop",

    ":components:keyedit:api",
    ":components:keyedit:impl",
    ":components:keyedit:noop",

    ":components:infrared:api",
    ":components:infrared:impl",
    ":components:infrared:editor",
    ":components:infrared:utils",

    ":components:firstpair:api",
    ":components:firstpair:impl",

    ":components:inappnotification:api",
    ":components:inappnotification:impl",

    ":components:settings:api",
    ":components:settings:impl",

    ":components:changelog:api",
    ":components:changelog:impl",

    ":components:updater:api",
    ":components:updater:impl",
    ":components:updater:screen",
    ":components:updater:downloader",
    ":components:updater:card",
    ":components:updater:subghz",

    ":components:nfceditor:api",
    ":components:nfceditor:impl",
    ":components:nfceditor:sample",

    ":components:wearable:core:ui:components",
    ":components:wearable:core:ui:theme",

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
    ":components:wearable:wearrootscreen:api",
    ":components:wearable:wearrootscreen:impl",

    ":components:widget:api",
    ":components:widget:screen",
    ":components:widget:impl",

    ":components:nfc:mfkey32:api",
    ":components:nfc:mfkey32:screen",
    ":components:nfc:tools:api",
    ":components:nfc:tools:impl",

    ":components:toolstab:api",
    ":components:toolstab:impl",
    ":components:faphub:appcard:api",
    ":components:faphub:appcard:composable",
    ":components:faphub:dao:api",
    ":components:faphub:dao:network",
    ":components:faphub:main:api",
    ":components:faphub:main:impl",
    ":components:faphub:catalogtab:api",
    ":components:faphub:catalogtab:impl",
    ":components:faphub:installedtab:api",
    ":components:faphub:installedtab:impl",
    ":components:faphub:search:api",
    ":components:faphub:search:impl",
    ":components:faphub:category:api",
    ":components:faphub:category:impl",
    ":components:faphub:uninstallbutton:api",
    ":components:faphub:uninstallbutton:impl",
    ":components:faphub:fapscreen:api",
    ":components:faphub:fapscreen:impl",
    ":components:faphub:installation:button:api",
    ":components:faphub:installation:button:impl",
    ":components:faphub:installation:manifest:api",
    ":components:faphub:installation:manifest:impl",
    ":components:faphub:installation:stateprovider:api",
    ":components:faphub:installation:stateprovider:impl",
    ":components:faphub:installation:queue:api",
    ":components:faphub:installation:queue:impl",
    ":components:faphub:installation:all:api",
    ":components:faphub:installation:all:impl",
    ":components:faphub:target:api",
    ":components:faphub:target:impl",
    ":components:faphub:utils",
    ":components:faphub:report:api",
    ":components:faphub:report:impl",
    ":components:faphub:errors:api",
    ":components:faphub:errors:impl",
    ":components:faphub:screenshotspreview:api",
    ":components:faphub:screenshotspreview:impl",

    ":components:selfupdater:api",
    ":components:selfupdater:impl",
    ":components:selfupdater:googleplay",
    ":components:selfupdater:unknown",
    ":components:selfupdater:debug",
    ":components:selfupdater:thirdparty:api",
    ":components:selfupdater:thirdparty:github",

    ":components:unhandledexception:api",
    ":components:unhandledexception:impl",

    ":components:notification:api",
    ":components:notification:impl",
    ":components:notification:noop",

    ":components:remote-controls:api-backend",
    ":components:remote-controls:api-backend-flipper",
    ":components:remote-controls:core-model",
    ":components:remote-controls:core-ui",
    ":components:remote-controls:main:impl",
    ":components:remote-controls:main:api",
    ":components:remote-controls:brands:impl",
    ":components:remote-controls:brands:api",
    ":components:remote-controls:categories:impl",
    ":components:remote-controls:categories:api",
    ":components:remote-controls:grid:main:impl",
    ":components:remote-controls:grid:main:api",
    ":components:remote-controls:grid:create-control:impl",
    ":components:remote-controls:grid:create-control:api",
    ":components:remote-controls:grid:remote:impl",
    ":components:remote-controls:grid:remote:api",
    ":components:remote-controls:grid:saved:impl",
    ":components:remote-controls:grid:saved:api",
    ":components:remote-controls:setup:impl",
    ":components:remote-controls:setup:api",

    ":components:rootscreen:api",
    ":components:rootscreen:impl",

    ":instances:android:baselineprofile"
)
