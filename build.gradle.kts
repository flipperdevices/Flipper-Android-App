import com.flipperdevices.buildlogic.plugins.ApkConfig

plugins {
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.square.anvil) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)

    id("flipper.apk-config")
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    ktlint {
        version.set(rootProject.libs.versions.ktlint.runtime)
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
