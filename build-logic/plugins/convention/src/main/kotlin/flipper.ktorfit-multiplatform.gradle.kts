plugins {
    id("flipper.multiplatform")
    id("com.google.devtools.ksp")
    id("de.jensklingenberg.ktorfit")
}
kotlin {
    sourceSets {
        @Suppress("UnusedPrivateProperty")
        val commonMain by getting {
            dependencies {
                implementation(libs.ktorfit.lib)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
}
