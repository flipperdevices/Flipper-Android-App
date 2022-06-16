plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("kotlin-kapt")
}
android {
    buildTypes {
        defaultConfig {
            buildConfigField(
                "String",
                "COUNTLY_URL",
                "\"${ApkConfig.COUNTLY_URL}\""
            )
            buildConfigField(
                "String",
                "COUNTLY_APP_KEY",
                "\"${ApkConfig.COUNTLY_APP_KEY}\""
            )
        }
    }
}
dependencies {
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)

    implementation(project.libs.countly)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
