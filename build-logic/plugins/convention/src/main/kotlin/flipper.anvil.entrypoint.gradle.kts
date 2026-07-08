pluginManager.apply("com.google.devtools.ksp")
pluginManager.apply("dev.zacsweers.metro")

dependencies {
    "implementation"(libs.metro.utils.annotations)
    "commonKsp"(libs.metro.utils.ksp)
}
