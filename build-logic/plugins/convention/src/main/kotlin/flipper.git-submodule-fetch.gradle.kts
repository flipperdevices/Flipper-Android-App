import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.ajoberstar.grgit.gradle.GrgitService

plugins {
    id("org.ajoberstar.grgit")
}

abstract class SubmoduleUpdateTask : DefaultTask() {
    @get:Input
    abstract val service: Property<GrgitService>

    @TaskAction
    fun execute() {
        println("Execute submodule update task")
        service.get().grgit.submodule()
    }
}

val registeredTask = tasks.register<SubmoduleUpdateTask>("submoduleUpdate") {
    service.set(grgitService.service)
}

tasks.named("preBuild").dependsOn(registeredTask)
