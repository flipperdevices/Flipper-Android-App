plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.sentry.gradle)
    implementation(libs.wire.gradle)
    implementation(libs.grgit.gradle)
    implementation(libs.kotlin.ksp.gradle)
    implementation(libs.metro.gradle)
    implementation(libs.compose.multiplatform.gradle)
    implementation(libs.compose.gradle)
    implementation(libs.kotlin.jvm.gradle)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlinpoet)
}

gradlePlugin {
    plugins {
        create("flipper.multiplatform-dependencies") {
            id = name
            implementationClass =
                "com.flipperdevices.buildlogic.plugin.mppdependencies.FlipperMultiplatformDependenciesPlugin"
        }
        create("flipper.java.version") {
            id = name
            implementationClass =
                "com.flipperdevices.buildlogic.plugin.JavaVersionPlugin"
        }
        create("flipper.compose-file-accessors") {
            id = name
            implementationClass =
                "com.flipperdevices.buildlogic.plugin.composefiles.ComposeFileAccessorsPlugin"
        }
    }
}
