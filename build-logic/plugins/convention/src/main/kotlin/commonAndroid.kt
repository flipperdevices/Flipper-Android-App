@file:Suppress("Filename")

import com.android.build.gradle.BaseExtension
import com.flipperdevices.buildlogic.ApkConfig
import com.flipperdevices.buildlogic.ApkConfig.VERSION_CODE
import com.flipperdevices.buildlogic.ApkConfig.VERSION_NAME
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val SPLASH_SCREEN_ACTIVITY = "com.flipperdevices.singleactivity.impl.SingleActivity"
private const val SPLASH_SCREEN_ACTIVITY_KEY = "splashScreenActivity"

fun BaseExtension.commonAndroid(target: Project) {
    configureDefaultConfig(target)
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()

    target.suppressOptIn()
}

@Suppress("UnstableApiUsage")
private fun BaseExtension.configureDefaultConfig(project: Project) {
    compileSdkVersion(ApkConfig.COMPILE_SDK_VERSION)
    defaultConfig {
        minSdk = ApkConfig.MIN_SDK_VERSION
        targetSdk = ApkConfig.TARGET_SDK_VERSION
        versionCode = project.VERSION_CODE
        versionName = project.VERSION_NAME

        consumerProguardFiles(
            "consumer-rules.pro"
        )

        packagingOptions {
            resources.excludes += "META-INF/LICENSE-LGPL-2.1.txt"
            resources.excludes += "META-INF/LICENSE-LGPL-3.txt"
            resources.excludes += "META-INF/LICENSE-W3C-TEST"
            resources.excludes += "META-INF/DEPENDENCIES"
            resources.excludes += "*.proto"
        }

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }
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
        }
        maybeCreate("release").apply {
            buildConfigField("boolean", "INTERNAL", "true")
        }
    }
}

@Suppress("UnstableApiUsage", "ForbiddenComment")
private fun BaseExtension.configureBuildFeatures() {
    // TODO: Disable by default
    //  BuildConfig is java source code. Java and Kotlin at one time affect build speed.
    buildFeatures.buildConfig = true
    buildFeatures.resValues = false
    buildFeatures.shaders = false
}

private fun BaseExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_11
    compileOptions.targetCompatibility = JavaVersion.VERSION_11
}

@Suppress("MaxLineLength")
private fun Project.suppressOptIn() {
    tasks.withType<KotlinCompile>()
        .configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)

                freeCompilerArgs.add("-Xexpect-actual-classes")

                optIn.addAll(
                    "com.google.accompanist.pager.ExperimentalPagerApi",
                    "androidx.compose.ui.ExperimentalComposeUiApi",
                    "androidx.compose.foundation.ExperimentalFoundationApi",
                    "kotlinx.serialization.ExperimentalSerializationApi",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "com.squareup.anvil.annotations.ExperimentalAnvilApi",
                    "kotlin.time.ExperimentalTime",
                    "kotlin.RequiresOptIn",
                    "androidx.compose.animation.ExperimentalAnimationApi",
                    "com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
                    "androidx.compose.foundation.layout.ExperimentalLayoutApi"
                )
            }
        }
}

/**
 * Adds a dependency to the 'internalImplementation' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.internalImplementation(dependencyNotation: Any): Dependency? =
    add("internalImplementation", dependencyNotation)

fun <BuildTypeT> NamedDomainObjectContainer<BuildTypeT>.debug(
    action: BuildTypeT.() -> Unit
) {
    maybeCreate("debug").action()
}

fun <BuildTypeT> NamedDomainObjectContainer<BuildTypeT>.internal(
    action: BuildTypeT.() -> Unit
) {
    maybeCreate("internal").action()
}

fun <BuildTypeT> NamedDomainObjectContainer<BuildTypeT>.release(
    action: BuildTypeT.() -> Unit
) {
    maybeCreate("release").action()
}
