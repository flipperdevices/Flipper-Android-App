import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val optIns = listOf(
    "com.google.accompanist.pager.ExperimentalPagerApi",
    "androidx.compose.ui.ExperimentalComposeUiApi",
    "androidx.compose.foundation.ExperimentalFoundationApi",
    "kotlinx.serialization.ExperimentalSerializationApi",
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
    "kotlin.time.ExperimentalTime",
    "kotlin.RequiresOptIn",
    "androidx.compose.material.ExperimentalMaterialApi",
    "androidx.compose.animation.ExperimentalAnimationApi",
    "com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
    "androidx.compose.foundation.layout.ExperimentalLayoutApi",
)
extensions
    .findByType<KotlinMultiplatformExtension>()
    ?.sourceSets
    ?.all { optIns.onEach(languageSettings::optIn) }

tasks.withType<KotlinCompile>()
    .configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xexpect-actual-classes")
            optIn.addAll(optIns)
        }
    }
