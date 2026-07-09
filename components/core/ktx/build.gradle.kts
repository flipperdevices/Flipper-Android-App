plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.buildKonfig)

    implementation(libs.kotlin.coroutines)
    implementation(libs.okio)
}

androidDependencies {
    implementation(libs.appcompat)
}

commonTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
}
