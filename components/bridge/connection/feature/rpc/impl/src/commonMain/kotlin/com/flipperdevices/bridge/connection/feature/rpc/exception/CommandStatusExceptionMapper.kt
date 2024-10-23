package com.flipperdevices.bridge.connection.feature.rpc.exception

import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcAppCantStartException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcAppCmdErrorException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcAppNotRunningException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcAppSystemLockedException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcBusyException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcContinuousCommandInterruptedException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcDecodeException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcGeneralException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcGpioModeIncorrectException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcGpioUnknownPinModeException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcInvalidParametersException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcNotImplementedException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageAlreadyOpenException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageDeniedException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageDirNotEmptyException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageExistException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageInternalException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageInvalidNameException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageInvalidParameterException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageNotExistException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageNotImplementedException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageNotReadyException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcVirtualDisplayAlreadyStartedException
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcVirtualDisplayNotStartedException
import com.flipperdevices.protobuf.CommandStatus
import com.flipperdevices.protobuf.Main

fun Main.resultOrError(): Result<Main> {
    val exception = asException()

    return if (exception == null) {
        Result.success(this)
    } else {
        Result.failure(exception)
    }
}

@Suppress("CyclomaticComplexMethod")
private fun Main.asException(): FRpcException? = when (command_status) {
    CommandStatus.ERROR_APP_CANT_START -> FRpcAppCantStartException(this)
    CommandStatus.ERROR_APP_CMD_ERROR -> FRpcAppCmdErrorException(this)
    CommandStatus.ERROR_APP_NOT_RUNNING -> FRpcAppNotRunningException(this)
    CommandStatus.ERROR_APP_SYSTEM_LOCKED -> FRpcAppSystemLockedException(this)
    CommandStatus.ERROR_BUSY -> FRpcBusyException(this)
    CommandStatus.ERROR_CONTINUOUS_COMMAND_INTERRUPTED ->
        FRpcContinuousCommandInterruptedException(this)

    CommandStatus.ERROR_DECODE -> FRpcDecodeException(this)
    CommandStatus.ERROR_GPIO_MODE_INCORRECT -> FRpcGpioModeIncorrectException(this)
    CommandStatus.ERROR_GPIO_UNKNOWN_PIN_MODE ->
        FRpcGpioUnknownPinModeException(this)

    CommandStatus.ERROR_INVALID_PARAMETERS -> FRpcInvalidParametersException(this)
    CommandStatus.ERROR_NOT_IMPLEMENTED -> FRpcNotImplementedException(this)
    CommandStatus.ERROR_STORAGE_ALREADY_OPEN ->
        FRpcStorageAlreadyOpenException(this)

    CommandStatus.ERROR_STORAGE_DENIED -> FRpcStorageDeniedException(this)
    CommandStatus.ERROR_STORAGE_DIR_NOT_EMPTY ->
        FRpcStorageDirNotEmptyException(this)

    CommandStatus.ERROR_STORAGE_EXIST -> FRpcStorageExistException(this)
    CommandStatus.ERROR_STORAGE_INTERNAL -> FRpcStorageInternalException(this)
    CommandStatus.ERROR_STORAGE_INVALID_NAME ->
        FRpcStorageInvalidNameException(this)

    CommandStatus.ERROR_STORAGE_INVALID_PARAMETER ->
        FRpcStorageInvalidParameterException(this)

    CommandStatus.ERROR_STORAGE_NOT_EXIST -> FRpcStorageNotExistException(this)
    CommandStatus.ERROR_STORAGE_NOT_IMPLEMENTED ->
        FRpcStorageNotImplementedException(this)

    CommandStatus.ERROR_STORAGE_NOT_READY -> FRpcStorageNotReadyException(this)
    CommandStatus.ERROR_VIRTUAL_DISPLAY_ALREADY_STARTED ->
        FRpcVirtualDisplayAlreadyStartedException(this)

    CommandStatus.ERROR_VIRTUAL_DISPLAY_NOT_STARTED ->
        FRpcVirtualDisplayNotStartedException(this)

    is CommandStatus.Unrecognized,
    CommandStatus.ERROR -> FRpcGeneralException(this)

    CommandStatus.OK -> null
}
