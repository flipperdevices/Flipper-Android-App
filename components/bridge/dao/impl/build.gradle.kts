plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("com.google.devtools.ksp")
}

android.namespace = "com.flipperdevices.bridge.dao.impl"

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

androidDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.okio)
    implementation(libs.okio.fake)

    // Testing
}

dependencies {
    "commonKsp"(libs.room.ksp)

    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(projects.components.core.buildKonfig)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockk)
    "androidUnitTestImplementation"(libs.mockito.kotlin)
    "androidUnitTestImplementation"(libs.ktx.testing)
    "androidUnitTestImplementation"(libs.roboelectric)
    "androidUnitTestImplementation"(libs.kotlin.coroutines.test)
}
