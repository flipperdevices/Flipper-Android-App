plugins {
    id("com.android.application")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:core:ktx"))
    implementation(project(":components:core:log"))
    implementation(project(":components:core:navigation"))
    implementation(project(":components:core:preference"))
    implementation(project(":components:core:ui"))

    implementation(project(":components:pair:api"))
    implementation(project(":components:pair:impl"))

    implementation(project(":components:info:api"))
    implementation(project(":components:info:impl"))

    implementation(project(":components:bottombar:api"))
    implementation(project(":components:bottombar:impl"))

    implementation(project(":components:filemanager:api"))
    implementation(project(":components:filemanager:impl"))

    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:bridge:service:impl"))

    implementation(project(":components:screenstreaming:api"))
    implementation(project(":components:screenstreaming:impl"))

    implementation(project(":components:share:api"))
    implementation(project(":components:share:receive"))
    implementation(project(":components:share:export"))

    implementation(project(":components:singleactivity:api"))
    implementation(project(":components:singleactivity:impl"))

    implementation(project(":components:deeplink:api"))
    implementation(project(":components:deeplink:impl"))

    implementation(project(":components:debug:api"))
    implementation(project(":components:debug:impl"))

    implementation(project(":components:archive:api"))
    implementation(project(":components:archive:impl"))

    implementation(project(":components:connection:api"))
    implementation(project(":components:connection:impl"))

    implementation(project(":components:synchronization:api"))
    implementation(project(":components:synchronization:impl"))

    implementation(project(":components:analytics:shake2report:api"))
    releaseImplementation(project(":components:analytics:shake2report:noop"))
    debugImplementation(project(":components:analytics:shake2report:impl"))
    add("internalImplementation", project(":components:analytics:shake2report:impl"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.MATERIAL)
    implementation(Libs.TREX)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.ACTIVITY_KTX)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.CICERONE)
    implementation(Libs.TIMBER)
}
