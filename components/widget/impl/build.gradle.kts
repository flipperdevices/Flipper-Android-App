plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.widget.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.bridge.pbutils)

    implementation(libs.work.ktx)
    implementation(libs.ktx)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.kotlin.coroutines)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
