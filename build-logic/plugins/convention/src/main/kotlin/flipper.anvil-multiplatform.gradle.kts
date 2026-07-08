import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("flipper.multiplatform")
}

pluginManager.apply("com.google.devtools.ksp")
pluginManager.apply("dev.zacsweers.metro")

configure<KotlinMultiplatformExtension> {
    sourceSets {
        val commonMain by getting

        commonMain.dependencies {
            implementation(libs.metro.utils.annotations)
        }
    }
}

dependencies {
    add("commonKsp", libs.metro.utils.ksp)
}
