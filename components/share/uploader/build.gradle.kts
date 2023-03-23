plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.share.api)
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.share)

    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.kotlin.serialization.json)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)

    // Dagger deps
    implementation(libs.dagger)
    implementation(project(mapOf("path" to ":components:keyedit:api")))
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
