plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.ui.decompose"

commonDependencies {
    implementation(projects.components.core.activityholder)

    implementation(projects.components.core.preference)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    implementation(libs.bundles.decompose)
    implementation(libs.essenty.lifecycle.coroutines)
    implementation(libs.kotlin.serialization.json)
}

androidDependencies {
    implementation(libs.compose.activity)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
}
