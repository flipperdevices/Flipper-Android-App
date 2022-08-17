plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    // implementation(libs.kotlin.ksp.gradle)
    // implementation(libs.kotlin.serialization.gradle)
    implementation(libs.ktlint.gradle)
    // implementation(libs.protobuf.gradle)
    implementation(libs.sentry.gradle)
    // implementation(libs.square.anvil.gradle)

    implementation(project(":plugins:apk-config"))
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
