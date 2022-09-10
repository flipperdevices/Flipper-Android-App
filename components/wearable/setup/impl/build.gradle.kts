plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.wearable.setup.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.wearable.core.ui.components)
    implementation(projects.components.wearable.core.ui.ktx)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)

    implementation(libs.wear)
    implementation(libs.wear.gms)
    implementation(libs.wear.interaction.phone)
    implementation(libs.wear.interaction.remote)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.compose.wear.navigation)
    implementation(libs.horologist.layout)
    implementation(libs.lifecycle.compose)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}
