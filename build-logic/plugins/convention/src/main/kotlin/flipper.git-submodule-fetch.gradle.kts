import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.ajoberstar.grgit.gradle.GrgitService
import org.gradle.api.services.ServiceReference

plugins {
    id("org.ajoberstar.grgit.service")
}

abstract class SubmoduleUpdateTask : DefaultTask() {
    @get:ServiceReference("grgit")
    abstract val service: Property<GrgitService>

    @TaskAction
    fun execute() {
        println("Execute submodule update task")
        service.get().grgit.submodule()
    }
}

val registeredTask = tasks.register<SubmoduleUpdateTask>("submoduleUpdate")

tasks.named("preBuild").dependsOn(registeredTask)
