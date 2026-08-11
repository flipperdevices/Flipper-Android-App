package com.flipperdevices.bridge.connection.livetests.model

sealed class LiveTestError(message: String) : Exception(message)

class FeatureNotSupportedError(
    featureName: String
) : LiveTestError("Feature $featureName is not supported by the connected device")

class LiveTestAssertionError(
    message: String
) : LiveTestError(message)
