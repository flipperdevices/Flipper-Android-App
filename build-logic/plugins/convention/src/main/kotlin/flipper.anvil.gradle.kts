import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

pluginManager.apply("com.google.devtools.ksp")
pluginManager.apply("dev.zacsweers.metro")

val metroAnnotations = libs.metro.utils.annotations

dependencies {
    add("commonKsp", libs.metro.utils.ksp)
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    configure<KotlinMultiplatformExtension> {
        sourceSets {
            val commonMain by getting
            commonMain.dependencies {
                implementation(metroAnnotations)
            }
        }
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    dependencies {
        "implementation"(metroAnnotations)
    }
}
