import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

configure<DetektExtension> {
    //allRules = true
    config = rootProject.files("config/detekt/detekt.yml")
}

dependencies {
    detektPlugins(libs.detekt.ruleset.compiler)
    detektPlugins(libs.detekt.ruleset.ktlint)
}