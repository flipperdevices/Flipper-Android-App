package com.flipperdevices.core.ui.errors.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.errors.impl.composable.ComposableFlipperNotConnectedError
import com.flipperdevices.core.ui.errors.impl.composable.ComposableGeneralError
import com.flipperdevices.core.ui.errors.impl.composable.ComposableNoNetworkError
import com.flipperdevices.core.ui.errors.impl.composable.ComposableNoServerError
import com.flipperdevices.core.ui.errors.impl.composable.ComposableWrongRequestError
import com.flipperdevices.faphub.errors.api.throwable.FirmwareNotSupported
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import java.net.UnknownHostException

@Composable
fun ComposableThrowableErrorInternal(
    throwable: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (throwable) {
        is UnknownHostException -> ComposableNoNetworkError(
            modifier = modifier,
            onRetry = onRetry
        )

        is FirmwareNotSupported -> {}

        is JsonConvertException,
        is ClientRequestException -> ComposableWrongRequestError(
            modifier = modifier,
            onRetry = onRetry
        )
        is FlipperNotConnected -> ComposableFlipperNotConnectedError(
            modifier = modifier,
            onRetry = onRetry
        )
        is ServerResponseException -> ComposableNoServerError(
            modifier = modifier,
            onRetry = onRetry
        )

        else -> ComposableGeneralError(
            modifier = modifier,
            onRetry = onRetry
        )
    }
}
