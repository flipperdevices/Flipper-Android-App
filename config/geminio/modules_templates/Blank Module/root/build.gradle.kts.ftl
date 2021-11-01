plugins {
    id("com.android.library")
    id("kotlin-android")
<#if shouldGenerateDI>
    id("com.squareup.anvil")
    id("kotlin-kapt")
</#if>
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()
<#if needCompose>
apply<com.flipperdevices.gradle.ComposerPlugin>()
</#if>

dependencies {


    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
<#if needCompose>

    // Compose
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)
</#if>
<#if shouldGenerateDI>

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
</#if>
}
