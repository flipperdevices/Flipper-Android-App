rootProject.name = "Flipper App"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

include(":instances:app")

include(":components:bridge:api")
include(":components:bridge:dao")
include(":components:bridge:impl")
include(":components:bridge:provider")
include(":components:bridge:service:impl")
include(":components:bridge:service:api")
include(":components:bridge:protobuf")

include(":components:filemanager:api")
include(":components:filemanager:impl")

include(":components:core:di")
include(":components:core:ktx")
include(":components:core:log")
include(":components:core:navigation")
include(":components:core:preference")
include(":components:core:ui")
include(":components:core:test")

include(":components:pair:api")
include(":components:pair:impl")

include(":components:bottombar:api")
include(":components:bottombar:impl")

include(":components:info:api")
include(":components:info:impl")

include(":components:analytics:shake2report:api")
include(":components:analytics:shake2report:noop")
include(":components:analytics:shake2report:impl")

include(":components:screenstreaming:impl")
include(":components:screenstreaming:api")

include(":components:share:api")
include(":components:share:export")
include(":components:share:common")
include(":components:share:receive")

include(":components:singleactivity:api")
include(":components:singleactivity:impl")

include(":components:deeplink:api")
include(":components:deeplink:impl")

include(":components:debug:api")
include(":components:debug:impl")

include(":components:archive:api")
include(":components:archive:impl")
