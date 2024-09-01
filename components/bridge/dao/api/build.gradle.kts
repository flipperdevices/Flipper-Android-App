plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android.namespace = "com.flipperdevices.bridge.dao.api"

compose.resources {
    publicResClass = true
}

commonDependencies {
    implementation(projects.components.core.kmpparcelize)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)
}

androidDependencies {
    implementation(projects.components.core.ui.res)
}
