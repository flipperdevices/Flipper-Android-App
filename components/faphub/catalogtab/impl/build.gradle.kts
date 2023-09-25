plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.catalogtab.impl"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler"
        )
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler"
        )
    }
}

dependencies {
    implementation(projects.components.faphub.catalogtab.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.pager)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.errors.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.target.api)

    implementation(projects.components.bridge.dao.api)
    implementation("io.reactivex.rxjava3:rxjava:3.1.7")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.paging)

    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    implementation(libs.kotlin.immutable.collections)
}
