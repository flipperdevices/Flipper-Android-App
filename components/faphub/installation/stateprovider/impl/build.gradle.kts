plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.faphub.installation.stateprovider.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.target.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.kotlin.coroutines.test)
}
