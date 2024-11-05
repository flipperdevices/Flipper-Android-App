plugins {
    id("flipper.multiplatform")
    id("dev.zacsweers.anvil")
    id("com.google.devtools.ksp")
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}

pluginManager.withPlugin("kotlin-kapt") {
    error("Please, use `id(\"flipper.anvil.kapt\")` instead")
}

kotlin {
    sourceSets {
        val commonMain by getting

        commonMain.dependencies {
            implementation(libs.dagger)
            implementation(libs.zacsweers.anvil.annotations)
            implementation(libs.anvil.utils.annotations)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.anvil.utils.compiler)
    add("kspAndroid", libs.anvil.utils.compiler)
    add("kspDesktop", libs.anvil.utils.compiler)
}
