plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.wearable.sync.common)
    implementation(projects.components.wearable.sync.handheld.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.wear.gms)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)
}
