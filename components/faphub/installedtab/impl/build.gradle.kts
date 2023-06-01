plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.faphub.installedtab.impl"

dependencies {
    implementation(projects.components.faphub.installedtab.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.data)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.errors)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.installation.stateprovider.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.paging)

    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.kotlin.immutable.collections)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}
