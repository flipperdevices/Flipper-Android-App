import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
}

configure<BaseExtension> {
    commonAndroid(project)
}

/**
 * KMP tasks are using allTests to run tests
 *
 * When KMP setup is not available at flipper.android-lib, we can create here custom task
 * which will run testDebugUnitTest
 */
tasks.findByName("testDebug")?.let { androidUnitTests ->
    tasks.register("allTests") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        dependsOn(androidUnitTests)
    }
}
