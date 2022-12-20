import com.flipperdevices.buildlogic.ApkConfig

plugins {
    id("flipper.android-lib")
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
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)

    implementation(libs.countly)
    implementation(libs.protobuf.metric)
    implementation(libs.ktor.client)
    implementation(libs.ktor.logging)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
