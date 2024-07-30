plugins {
    id("flipper.android-lib")
    id("com.google.devtools.ksp")
    id("de.jensklingenberg.ktorfit")
}

dependencies {
    "implementation"(libs.ktorfit.lib)
    ksp(libs.ktorfit.ksp)
}
