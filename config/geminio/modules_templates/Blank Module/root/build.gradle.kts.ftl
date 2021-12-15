plugins {
    id("com.android.library")
    id("kotlin-android")
<#if shouldGenerateDI>
    id("com.squareup.anvil")
    id("kotlin-kapt")
</#if>
}

<#if needCompose>

</#if>

dependencies {
<#if shouldGenerateDI>
    implementation(project(":components:core:di"))
</#if>
<#if needCompose>
    implementation(project(":components:core:ui"))
</#if>
<#if isApi>
    implementation(Libs.CICERONE)
</#if>
<#if needCompose>

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
</#if>
<#if shouldGenerateDI>

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
</#if>
<#if needFragment>

    implementation(Libs.CICERONE)
</#if>
}
