plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyparser.impl"

androidDependencies {
    implementation(projects.components.keyparser.api)
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)

    // Testing
}

dependencies {
    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(projects.components.core.buildKonfig)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockk)
    "androidUnitTestImplementation"(libs.ktx.testing)
    "androidUnitTestImplementation"(libs.roboelectric)
    "androidUnitTestImplementation"(libs.lifecycle.test)
    "androidUnitTestImplementation"(libs.kotlin.coroutines.test)
}
