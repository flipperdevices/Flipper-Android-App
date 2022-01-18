plugins {
<#if needCompose>
    androidCompose
<#else>
    androidLibrary
</#if>
<#if shouldGenerateDI>
    id("com.squareup.anvil")
    id("kotlin-kapt")
</#if>
}

<#if needCompose>

</#if>

dependencies {
<#if shouldGenerateDI>
    implementation(projects.components.core.di)
</#if>
<#if needCompose>
    implementation(projects.components.core.ui)
</#if>
<#if isApi>

    implementation(Libs.CICERONE)
<#else>
<#if needFragment>

    implementation(Libs.CICERONE)
    implementation(Libs.APPCOMPAT)
</#if>
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
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
</#if>
}
