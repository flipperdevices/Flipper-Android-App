import com.google.devtools.ksp.gradle.KspExtension

plugins {
    id("dev.zacsweers.anvil")
}

anvil {
    useKsp(
        contributesAndFactoryGeneration = true,
        componentMerging = true,
    )
}

the<KspExtension>().apply {
    arg("anvil.ksp.generateShims", "false")
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "commonKsp"(libs.anvil.utils.compiler)
}
