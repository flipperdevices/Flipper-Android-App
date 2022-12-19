import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

configure<DetektExtension> {
    //allRules = true
    parallel = true

    config = rootProject.files("config/detekt/detekt.yml")

}

tasks.register<Detekt>("detektFormat") {
    autoCorrect = true
}

tasks.withType<Detekt> {
    // Disable caching
    outputs.upToDateWhen { false }

    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(false)
    }

    setSource(files(projectDir))

    include("**/*.kt", "**/*.kts")
    exclude(
        "**/resources/**",
        "**/build/**",
    )

    // Target version of the generated JVM bytecode. It is used for type resolution.
    this.jvmTarget = "1.8"
}

dependencies {
    detektPlugins(libs.detekt.ruleset.compiler)
    detektPlugins(libs.detekt.ruleset.ktlint)
}