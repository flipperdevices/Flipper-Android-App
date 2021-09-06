buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(GradlePlugins.KOTLIN_PLUGIN)
        classpath(GradlePlugins.ANDROID_GRADLE_PLUGIN)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}