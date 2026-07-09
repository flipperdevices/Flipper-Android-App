@file:Suppress("Filename")

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryVariantDimension
import com.flipperdevices.buildlogic.ApkConfig
import com.flipperdevices.buildlogic.ApkConfig.VERSION_CODE
import com.flipperdevices.buildlogic.ApkConfig.VERSION_NAME
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val SPLASH_SCREEN_ACTIVITY = "com.flipperdevices.singleactivity.impl.SingleActivity"
private const val SPLASH_SCREEN_ACTIVITY_KEY = "splashScreenActivity"

fun CommonExtension.commonAndroid(target: Project) {
    configureDefaultConfig(target)
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()

    target.suppressOptIn()
}

@Suppress("UnstableApiUsage")
private fun CommonExtension.configureDefaultConfig(project: Project) {
    compileSdk = ApkConfig.COMPILE_SDK_VERSION
    defaultConfig.minSdk = ApkConfig.MIN_SDK_VERSION
    defaultConfig.manifestPlaceholders[SPLASH_SCREEN_ACTIVITY_KEY] = SPLASH_SCREEN_ACTIVITY

    if (this is ApplicationExtension) {
        defaultConfig.targetSdk = ApkConfig.TARGET_SDK_VERSION
        defaultConfig.versionCode = project.VERSION_CODE
        defaultConfig.versionName = project.VERSION_NAME
    }

    val commonDefaultConfig = defaultConfig
    if (commonDefaultConfig is LibraryVariantDimension) {
        val consumerRules = project.layout.projectDirectory.file("consumer-rules.pro").asFile
        if (consumerRules.exists()) {
            commonDefaultConfig.consumerProguardFiles(consumerRules)
        }
    }

    packaging.resources.excludes += "META-INF/LICENSE-LGPL-2.1.txt"
    packaging.resources.excludes += "META-INF/LICENSE-LGPL-3.txt"
    packaging.resources.excludes += "META-INF/LICENSE-W3C-TEST"
    packaging.resources.excludes += "META-INF/DEPENDENCIES"
    packaging.resources.excludes += "*.proto"

    testOptions.unitTests.isIncludeAndroidResources = true
}

private fun CommonExtension.configureBuildTypes() {
    buildTypes.apply {
        maybeCreate("debug").apply {
            buildConfigField("boolean", "INTERNAL", "true")
            if (this is ApplicationBuildType) {
                isDebuggable = true
            }
        }
        maybeCreate("internal").apply {
            matchingFallbacks += "debug"
            sourceSets.getByName(this.name).setRoot("src/debug")

            buildConfigField("boolean", "INTERNAL", "true")
        }
        maybeCreate("release").apply {
            buildConfigField("boolean", "INTERNAL", "true")
        }
    }
}

@Suppress("UnstableApiUsage", "ForbiddenComment")
private fun CommonExtension.configureBuildFeatures() {
    // TODO: Disable by default
    //  BuildConfig is java source code. Java and Kotlin at one time affect build speed.
    buildFeatures.buildConfig = true
    buildFeatures.resValues = false
    buildFeatures.shaders = false
}

private fun CommonExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_11
    compileOptions.targetCompatibility = JavaVersion.VERSION_11
}

@Suppress("MaxLineLength")
fun Project.suppressOptIn() {
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
 * Gradle 9 fails test tasks that discover no tests; most KMP modules have empty host/desktop
 * test compilations, so the failure is downgraded here
 */
fun Project.ignoreNoDiscoveredTests() {
    tasks.withType<AbstractTestTask>().configureEach {
        failOnNoDiscoveredTests.set(false)
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

fun <BuildTypeT : BuildType> NamedDomainObjectContainer<BuildTypeT>.debug(
    action: BuildTypeT.() -> Unit
) {
    maybeCreate("debug").action()
}

fun <BuildTypeT : BuildType> NamedDomainObjectContainer<BuildTypeT>.internal(
    action: BuildTypeT.() -> Unit
) {
    maybeCreate("internal").action()
}

fun <BuildTypeT : BuildType> NamedDomainObjectContainer<BuildTypeT>.release(
    action: BuildTypeT.() -> Unit
) {
    maybeCreate("release").action()
}
