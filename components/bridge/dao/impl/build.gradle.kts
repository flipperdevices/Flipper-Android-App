plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("com.google.devtools.ksp")
}

android.namespace = "com.flipperdevices.bridge.dao.impl"

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    commonKsp(libs.room.ksp)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.okio)
    implementation(libs.okio.fake)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.kotlin.coroutines.test)
}
