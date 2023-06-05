plugins {
    id("de.jensklingenberg.ktorfit")
    id("com.google.devtools.ksp")
}

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = libs.versions.ktorfit.asProvider().get()
}

dependencies {
    "implementation"(libs.ktorfit.lib)
    ksp(libs.ktorfit.ksp)
}
