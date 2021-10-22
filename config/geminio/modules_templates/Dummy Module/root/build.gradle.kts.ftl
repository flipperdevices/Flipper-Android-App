plugins {
    id("com.android.library")
    id("kotlin-android")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()
<#if needCompose>
apply<com.flipperdevices.gradle.ComposerPlugin>()
</#if>

dependencies {
    implementation(project(":components:core"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    <#if needCompose>
    // Compose
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)
    </#if>
}
