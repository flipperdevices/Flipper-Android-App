import com.flipperdevices.buildlogic.ApkConfig.COUNTLY_APP_KEY
import com.flipperdevices.buildlogic.ApkConfig.COUNTLY_URL

plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.metric.impl"
android {
    buildTypes {
        defaultConfig {
            buildConfigField(
                "String",
                "COUNTLY_URL",
                "\"${COUNTLY_URL}\""
            )
            buildConfigField(
                "String",
                "COUNTLY_APP_KEY",
                "\"${COUNTLY_APP_KEY}\""
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
    implementation(libs.ktor.client)
    implementation(libs.ktor.logging)
}
