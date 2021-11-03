rootProject.name = "Flipper App"
include(":components:bridge:api")
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

include(":instances:app")
include(":components:analytics:shake2report:api")
include(":components:analytics:shake2report:noop")
include(":components:analytics:shake2report:impl")
