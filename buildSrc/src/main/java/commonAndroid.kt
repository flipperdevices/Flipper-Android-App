import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

private const val SPLASH_SCREEN_ACTIVITY = "com.flipperdevices.app.SplashScreen"
private const val SPLASH_SCREEN_ACTIVITY_KEY = "splashScreenActivity"

fun BaseExtension.commonAndroid(target: Project) {
    buildToolsVersion = "31.0.0"

    configureDefaultConfig()
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()

    enableCompose()
}

private fun BaseExtension.configureDefaultConfig() {
    compileSdkVersion(ApkConfig.COMPILE_SDK_VERSION)
    defaultConfig {
        minSdk = ApkConfig.MIN_SDK_VERSION
        targetSdk = ApkConfig.TARGET_SDK_VERSION
        versionCode = ApkConfig.VERSION_CODE
        versionName = ApkConfig.VERSION_NAME

        consumerProguardFiles(
            "consumer-rules.pro"
        )
    }
}

private fun BaseExtension.configureBuildTypes() {
    buildTypes {
        defaultConfig {
            manifestPlaceholders[SPLASH_SCREEN_ACTIVITY_KEY] = SPLASH_SCREEN_ACTIVITY
        }
        maybeCreate("debug").apply {
            buildConfigField("boolean", "INTERNAL", "true")
            multiDexEnabled = true
            isDebuggable = true
        }
        maybeCreate("internal").apply {
            setMatchingFallbacks("debug")
            sourceSets.getByName(this.name).setRoot("src/debug")

            buildConfigField("boolean", "INTERNAL", "true")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        maybeCreate("release").apply {
            buildConfigField("boolean", "INTERNAL", "false")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

private fun BaseExtension.configureBuildFeatures() {
    buildFeatures.viewBinding = true
}

private fun BaseExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
    compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
}

// To speed up build need add flag for enable/disable compose for each module
private fun BaseExtension.enableCompose() {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = Versions.ANDROID_JETPACK_COMPOSE
}
