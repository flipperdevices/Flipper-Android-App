plugins {
    id("com.squareup.anvil")
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}

pluginManager.withPlugin("kotlin-kapt") {
    error("Please, use `id(\"flipper.anvil.kapt\")` instead")
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "ksp"(libs.anvil.utils.compiler)
}
