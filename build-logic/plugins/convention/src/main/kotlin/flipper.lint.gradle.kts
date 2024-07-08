import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt")
}

tasks.register<Detekt>("detektFormat") {
    autoCorrect = true
}

tasks.withType<Detekt> {
    // Disable caching
    outputs.upToDateWhen { false }

    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
    }

    setSource(files(projectDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    include("**/*.kt", "**/*.kts")
    exclude(
        "**/resources/**",
        "**/build/**",
    )

    parallel = true

    buildUponDefaultConfig = true

    allRules = true

    // Target version of the generated JVM bytecode. It is used for type resolution.
    this.jvmTarget = "1.8"
}

dependencies {
    detektPlugins(libs.detekt.ruleset.compiler)
    detektPlugins(libs.detekt.ruleset.ktlint)
    detektPlugins(libs.detekt.ruleset.compose)
    detektPlugins(libs.detekt.ruleset.decompose)
}
