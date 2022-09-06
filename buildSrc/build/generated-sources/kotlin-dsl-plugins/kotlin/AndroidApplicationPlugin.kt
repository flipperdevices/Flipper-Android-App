/**
 * Precompiled [androidApplication.gradle.kts][AndroidApplication_gradle] script plugin.
 *
 * @see AndroidApplication_gradle
 */
class AndroidApplicationPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("AndroidApplication_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
