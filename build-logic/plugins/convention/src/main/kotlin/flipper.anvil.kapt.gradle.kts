import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.squareup.anvil")
}

tasks.withType<KaptGenerateStubsTask>().configureEach {
    // TODO necessary until anvil supports something for K2 contribution merging
    compilerOptions {
        progressiveMode.set(false)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    // TODO necessary until anvil supports something for K2 contribution merging
    compilerOptions {
        progressiveMode.set(false)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "anvil"(libs.anvil.utils.compiler)
}
