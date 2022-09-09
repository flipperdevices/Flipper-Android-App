plugins {
    id("flipper.lint")
    id("flipper.android-app")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)

    implementation(libs.appcompat)
    implementation(libs.timber)
    implementation(libs.datastore)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
    implementation(libs.tangle.fragment.api)
    anvil(libs.tangle.fragment.compiler)
}
