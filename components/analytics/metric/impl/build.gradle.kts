plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("flipper.wire")
}

androidDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)

    implementation(libs.countly)
    implementation(libs.ktor.client)
    implementation(libs.ktor.logging)
}
