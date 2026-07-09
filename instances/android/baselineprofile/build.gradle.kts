import com.flipperdevices.buildlogic.ApkConfig
import com.flipperdevices.buildlogic.ApkConfig.TARGET_APP_ID_KEY

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baselineprofile)
}

android {
    commonAndroid(project)
    namespace = "${ApkConfig.APPLICATION_ID}.baselineprofile"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = projects.instances.android.app.path

    testOptions.managedDevices.localDevices {
        create("pixel6Api31") {
            device = "Pixel 6"
            apiLevel = 31
            systemImageSource = "google-atd"
        }
    }
}

baselineProfile {
    managedDevices += "pixel6Api31"
    useConnectedDevices = false
}

dependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(projects.components.core.log)
    implementation(libs.ktx.testing)
    implementation(libs.espresso.core)
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
}

androidComponents {
    onVariants { variant ->
        val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
        variant.instrumentationRunnerArguments.put(
            TARGET_APP_ID_KEY,
            variant.testedApks.map { dir -> artifactsLoader.load(dir)?.applicationId }
        )
    }
}
