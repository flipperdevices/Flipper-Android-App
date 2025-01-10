import com.flipperdevices.buildlogic.ApkConfig.VERSION_NAME
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("flipper.lint")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("flipper.multiplatform-dependencies")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.anvil")
}

kotlin {
    jvm("desktop") {
        withJava()
    }

    sourceSets {
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

includeCommonKspConfigurationTo("kspDesktop")

compose.desktop {
    application {
        mainClass = "com.flipperdevices.bridge.connection.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Flipper App"
            packageVersion = project.VERSION_NAME
        }
    }
}

commonDependencies {
    implementation(projects.components.bridge.connection.sample.shared)

    implementation(libs.kotlin.coroutines.swing)
}

anvil {
    useKsp(
        contributesAndFactoryGeneration = true,
        componentMerging = true,
    )
}

dependencies {
    "implementation"(libs.dagger)
    "implementation"(libs.anvil.utils.annotations)
    "commonKsp"(libs.anvil.utils.compiler)
}

dependencies {
    ksp(libs.dagger.compiler)
}