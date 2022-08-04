plugins {
<#if needCompose>
    id("androidCompose")
<#else>
    id("androidLibrary")
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
<#if isApi>

    implementation(libs.cicerone)
<#else>
<#if needFragment>

    implementation(libs.cicerone)
    implementation(libs.appcompat)
    implementation(projects.components.core.ui.fragment)
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
