import gradle.kotlin.dsl.accessors._3ac7b6c2885a95ee899a21e4f3ca95a4.kotlin

plugins {
    id("flipper.multiplatform")
    id("com.google.devtools.ksp")
    id("de.jensklingenberg.ktorfit")
}
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktorfit.lib)
            }
        }
    }
}
