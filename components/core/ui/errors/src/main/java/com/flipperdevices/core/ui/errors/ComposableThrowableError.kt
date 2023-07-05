package com.flipperdevices.core.ui.errors

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.errors.composable.ComposableFlipperNotConnectedError
import com.flipperdevices.core.ui.errors.composable.ComposableGeneralError
import com.flipperdevices.core.ui.errors.composable.ComposableNoNetworkError
import com.flipperdevices.core.ui.errors.composable.ComposableNoServerError
import com.flipperdevices.core.ui.errors.composable.ComposableWrongRequestError
import com.flipperdevices.core.ui.errors.throwable.FlipperNotConnected
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import java.net.UnknownHostException

@Composable
fun ComposableThrowableError(
    throwable: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (throwable) {
        is UnknownHostException -> ComposableNoNetworkError(
            modifier = modifier,
            onRetry = onRetry
        )

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
