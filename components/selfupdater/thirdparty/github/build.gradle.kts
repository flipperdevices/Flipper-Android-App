import com.flipperdevices.buildlogic.ApkConfig.IS_GOOGLE_FEATURE_AVAILABLE

plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.selfupdater.thirdparty.github"

android {
    val isGoogleFeatureAvailable = project.IS_GOOGLE_FEATURE_AVAILABLE.toString()

    buildTypes {
        defaultConfig {
            buildConfigField("boolean", "IS_GOOGLE_FEATURE_AVAILABLE", isGoogleFeatureAvailable)
        }
    }
}

dependencies {
    implementation(projects.components.selfupdater.thirdparty.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(libs.lifecycle.runtime.ktx)

    // Ktor deps
    implementation(libs.kotlin.serialization.json)
    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)

    // Dagger deps
    implementation(projects.components.core.di)
}
