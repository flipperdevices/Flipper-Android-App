plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    kotlin("android") apply false
    id("io.gitlab.arturbosch.detekt") version GradlePlugins.DETEKT
    id("org.jlleitschuh.gradle.ktlint") version GradlePlugins.KTLINT
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    ktlint {
        version.set(Versions.KTLINT)
        android.set(true)
        verbose.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    detekt {
        config = rootProject.files("config/detekt/detekt.yml")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.register("dumpApkVersion") {
    val CODE_PROP = "version_code"
    val NAME_PROP = "version_name"

    val outputFileProvider = layout.buildDirectory.file("version/apk-version.properties")

    inputs.property(CODE_PROP, provider { ApkConfig.VERSION_CODE })
    inputs.property(NAME_PROP, provider { ApkConfig.VERSION_NAME })
    outputs.file(outputFileProvider)

    doLast {
        val outputFile = outputFileProvider.get().asFile
        outputFile.deleteOnExit()
        outputFile.parentFile.mkdirs()

        val props = java.util.Properties()
        props.setProperty(CODE_PROP, inputs.properties[CODE_PROP].toString())
        props.setProperty(NAME_PROP, inputs.properties[NAME_PROP].toString())
        props.store(outputFile.writer(), /* comments = */ null)
    }
}
