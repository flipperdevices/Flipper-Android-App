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

tasks.register<SubmoduleUpdateTask>("submoduleUpdate") {
    service.set(grgitService.service)
}
