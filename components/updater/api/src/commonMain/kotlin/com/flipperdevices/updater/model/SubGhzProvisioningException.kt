package com.flipperdevices.updater.model

class SubGhzProvisioningException(
    val errorCode: Int,
    message: String
) : RuntimeException(message)
