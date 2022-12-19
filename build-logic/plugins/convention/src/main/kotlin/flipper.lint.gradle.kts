import org.gradle.kotlin.dsl.configure
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

configure<DetektExtension> {
    config = rootProject.files("config/detekt/detekt.yml")
}