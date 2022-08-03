import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

// Workaround for https://github.com/gradle/gradle/issues/15383
fun Project.resolveVersion(name: String) =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findVersion(name)
        .get().requiredVersion
