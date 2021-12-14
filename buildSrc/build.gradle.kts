plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

val kotlinVersion: String = libs.versions.kotlin.get()

dependencies {
    compileOnly(gradleApi())

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.annotation.processing)
    implementation(libs.kotlin.compiler)
    implementation(libs.kotlin.gradle.pluginApi)

    implementation(kotlin("gradle-plugin", version = kotlinVersion))
    implementation(kotlin("stdlib", version = kotlinVersion))
    implementation(kotlin("stdlib-common", version = kotlinVersion))
    implementation(kotlin("stdlib-jdk7", version = kotlinVersion))
    implementation(kotlin("stdlib-jdk8", version = kotlinVersion))
    implementation(kotlin("reflect", version = kotlinVersion))

    implementation(libs.android.gradle)
    implementation(libs.square.anvil.gradle)
    implementation(libs.protobuf.gradle)
}

configurations.all {
    resolutionStrategy {

        eachDependency {
            when {
                requested.group == "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
            }
        }
    }
}
