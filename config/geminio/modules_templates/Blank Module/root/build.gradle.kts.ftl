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
<#if needFragment>

    implementation(Libs.CICERONE)
</#if>
}
