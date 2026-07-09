plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(libs.kotlin.immutable.collections)
}

commonTestDependencies {
    implementation(libs.junit)
    implementation(libs.mockito.kotlin)
}
