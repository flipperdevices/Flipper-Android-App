plugins {
    id("de.jensklingenberg.ktorfit")
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"(libs.ktorfit.lib)
    ksp(libs.ktorfit.ksp)
}
