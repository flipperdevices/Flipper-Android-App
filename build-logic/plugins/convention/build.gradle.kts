plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.sentry.gradle)
    implementation(libs.protobuf.gradle)
    implementation(libs.grgit.gradle)
    implementation(libs.kotlin.ksp.gradle)
    implementation(libs.zacsweers.anvil.gradle)
    implementation(libs.compose.multiplatform.gradle)
    implementation(libs.compose.gradle)
    implementation(libs.kotlin.jvm.gradle)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        create("flipper.multiplatform-dependencies") {
            id = name
            implementationClass = "com.flipperdevices.buildlogic.plugin.FlipperMultiplatformDependenciesPlugin"
        }
    }
}
