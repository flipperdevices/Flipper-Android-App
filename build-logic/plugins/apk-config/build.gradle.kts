plugins {
    `kotlin-dsl`
}

val kotlinVersion: String = libs.versions.kotlin.general.get()

dependencies {
    compileOnly(gradleApi())

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.annotation.processing)
    implementation(libs.kotlin.compiler)
    implementation(libs.kotlin.gradle.pluginApi)
    implementation(libs.kotlin.serialization.gradle)
    implementation(libs.kotlin.ksp.gradle)

    implementation(kotlin("gradle-plugin", version = kotlinVersion))
    implementation(kotlin("stdlib", version = kotlinVersion))
    implementation(kotlin("stdlib-common", version = kotlinVersion))
    implementation(kotlin("stdlib-jdk7", version = kotlinVersion))
    implementation(kotlin("reflect", version = kotlinVersion))

    implementation(libs.android.gradle)
    implementation(libs.square.anvil.gradle)
    implementation(libs.protobuf.gradle)
    implementation(libs.sentry.gradle)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins.register("flipper.apk-config") {
        id = "flipper.apk-config"
        implementationClass = "com.flipperdevices.buildlogic.plugins.ApkConfigPlugin"
    }
}
