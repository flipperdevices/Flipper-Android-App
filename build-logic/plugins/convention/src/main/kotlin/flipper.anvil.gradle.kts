plugins {
    id("dev.zacsweers.anvil")
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "commonKsp"(libs.anvil.utils.compiler)
}
