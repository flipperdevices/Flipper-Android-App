import com.flipperdevices.buildlogic.ApkConfig

tasks.register("dumpApkVersion") {
    val CODE_PROP = "version_code"
    val NAME_PROP = "version_name"

    val outputFileProvider = layout.buildDirectory.file("version/apk-version.properties")

    inputs.property(CODE_PROP, provider { ApkConfig.VERSION_CODE })
    inputs.property(NAME_PROP, provider { ApkConfig.VERSION_NAME })
    outputs.file(outputFileProvider)

    doLast {
        val outputFile = outputFileProvider.get().asFile
        outputFile.deleteOnExit()
        outputFile.parentFile.mkdirs()

        val props = java.util.Properties()
        props.setProperty(CODE_PROP, inputs.properties[CODE_PROP].toString())
        props.setProperty(NAME_PROP, inputs.properties[NAME_PROP].toString())
        props.store(outputFile.writer(), /* comments = */ null)
    }
}
