plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("dev.zacsweers.metro")
}

jvmSharedTestDependencies {
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
}
