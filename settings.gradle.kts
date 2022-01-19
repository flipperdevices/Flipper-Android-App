rootProject.name = "FlipperApp"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

include(
    ":instances:app",
    ":components:bridge:api",
    ":components:bridge:impl",
    ":components:bridge:provider",
    ":components:bridge:service:impl",
    ":components:bridge:service:api",
    ":components:bridge:protobuf",
    ":components:bridge:synchronization:api",
    ":components:bridge:synchronization:impl",
    ":components:bridge:dao:api",
    ":components:bridge:dao:impl",

    ":components:filemanager:api",
    ":components:filemanager:impl",

    ":components:core:di",
    ":components:core:ktx",
    ":components:core:log",
    ":components:core:navigation",
    ":components:core:preference",
    ":components:core:ui",
    ":components:core:test",

    ":components:pair:api",
    ":components:pair:impl",

    ":components:bottombar:api",
    ":components:bottombar:impl",

    ":components:info:api",
    ":components:info:impl",

    ":components:analytics:shake2report:api",
    ":components:analytics:shake2report:noop",
    ":components:analytics:shake2report:impl",

    ":components:screenstreaming:impl",
    ":components:screenstreaming:api",

    ":components:share:api",
    ":components:share:export",
    ":components:share:common",
    ":components:share:receive",

    ":components:singleactivity:api",
    ":components:singleactivity:impl",

    ":components:deeplink:api",
    ":components:deeplink:impl",

    ":components:debug:api",
    ":components:debug:impl",

    ":components:archive:api",
    ":components:archive:impl",

    ":components:connection:api",
    ":components:connection:impl",

    ":components:keyscreen:api",
    ":components:keyscreen:impl",
)
