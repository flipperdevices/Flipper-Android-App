plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "${packageName}.impl"

dependencies {
    implementation(projects.components.${__moduleName}.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)


    // ViewModel
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

<#if needSerialization>
    // Serialization
    implementation(libs.kotlin.serialization.json)
</#if>

<#if needTest>
    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
</#if>
}
