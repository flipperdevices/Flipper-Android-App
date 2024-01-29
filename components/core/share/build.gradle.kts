plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.share"

android {
    defaultConfig {
        val applicationId = com.flipperdevices.buildlogic.ApkConfig.APPLICATION_ID
        val shareFileAuthorities = "$applicationId.filemanager.export.provider"
        manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
        buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
    }
}


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ktx)
    implementation(libs.annotations)
    implementation(libs.appcompat)
}
            }
        }
    }
