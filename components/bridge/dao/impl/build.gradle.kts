plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("com.google.devtools.ksp")
}


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
    commonKsp(libs.room.ksp)
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(projects.components.core.buildKonfig)
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.mockito.kotlin)
    implementation(libs.ktx.testing)
    implementation(libs.roboelectric)
    implementation(libs.kotlin.coroutines.test)
}
