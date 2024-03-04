package com.flipperdevices.core.test

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModelCoroutineScopeProvider
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.CoroutineScope

fun DecomposeViewModelCoroutineScopeProvider.mockScope(scope: CoroutineScope) {
    mockkObject(DecomposeViewModelCoroutineScopeProvider) {
        every {
            provideCoroutineScope(any(), any())
        } returns scope
    }
}
