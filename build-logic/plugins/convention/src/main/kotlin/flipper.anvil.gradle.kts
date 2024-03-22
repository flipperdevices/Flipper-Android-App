plugins {
    id("com.squareup.anvil")
}

anvil {
    generateDaggerFactories.set(true)
}

pluginManager.withPlugin("kotlin-kapt") {
    anvil {
        generateDaggerFactories.set(false)
    }
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "anvil"(libs.anvil.utils.compiler)
}
