import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val SPLASH_SCREEN_ACTIVITY = "com.flipperdevices.app.SplashScreen"
private const val SPLASH_SCREEN_ACTIVITY_KEY = "splashScreenActivity"

fun BaseExtension.commonAndroid(target: Project) {
    buildToolsVersion = "31.0.0"

    configureDefaultConfig()
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()

    target.suppressOptIn()
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

private fun Project.suppressOptIn() {
    tasks.withType<KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = "1.8"

                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi",
                    "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-Xopt-in=kotlin.RequiresOptIn",
                )
            }
        }
}
