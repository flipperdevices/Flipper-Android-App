plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.wearable.setup.api)
    implementation(projects.components.wearable.emulate.common)
    implementation(projects.components.wearable.emulate.wear.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.wearable.core.ui.ktx)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyscreen.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)

    implementation(libs.wear)
    implementation(libs.wear.gms)
    implementation(libs.wear.interaction.phone)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.placeholder)
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
