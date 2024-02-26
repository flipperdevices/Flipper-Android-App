plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.sentry.gradle)
    implementation(libs.protobuf.gradle)
    implementation(libs.grgit.gradle)
    implementation(libs.kotlin.ksp.gradle)
    implementation(libs.square.anvil.gradle)
    implementation(libs.ktorfit.gradle)
    implementation(libs.compose.multiplatform.gradle)
    implementation(libs.kotlin.jvm.gradle)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
