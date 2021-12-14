plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:analytics:shake2report:api"))
    implementation(project(":components:core:ktx"))
    implementation(project(":components:core:di"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)

    implementation(Libs.SEISMIC)
    implementation(Libs.TIMBER)
    implementation(Libs.TREESSENCE)
    implementation(Libs.SENTRY)
    implementation(Libs.SENTRY_TIMBER)
    implementation(Libs.ZIP4J)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}
