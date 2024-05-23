import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.squareup.anvil")
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
}

pluginManager.withPlugin("kotlin-kapt") {
    error("Please, use `id(\"flipper.anvil.kapt\")` instead")
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "anvil"(libs.anvil.utils.compiler)
}
