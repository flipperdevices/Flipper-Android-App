package com.flipperdevices.bridge.service.impl.di

import android.content.Context
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.delegates.FlipperLagsDetector
import com.flipperdevices.bridge.impl.manager.FlipperBleManagerImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.RestartRPCApiImpl
import com.flipperdevices.bridge.impl.manager.service.request.FlipperRequestApiImpl
import com.flipperdevices.bridge.service.impl.FlipperServiceApiImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperActionNotifierImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperLagsDetectorImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperSafeConnectWrapper
import com.flipperdevices.bridge.service.impl.delegate.FlipperServiceConnectDelegate
import kotlinx.coroutines.CoroutineScope
import javax.inject.Provider

class FlipperBleServiceComponentImpl(
    deps: FlipperBleServiceComponentDependencies,
    context: Context,
    scope: CoroutineScope,
    serviceErrorListener: FlipperServiceErrorListener
) : FlipperBleServiceComponent,
    FlipperBleServiceComponentDependencies by deps {

    private val flipperActionNotifier by lazy {
        FlipperActionNotifierImpl(scope = scope)
    }

    private val restartRPCApiImpl by lazy {
        RestartRPCApiImpl(
            serviceApiProvider = { serviceApiImpl.get() }
        )
    }

    private val flipperInformationApiImpl by lazy {
        FlipperInformationApiImpl(
            scopeProvider = { scope },
            metricApiProvider = { metricApi },
            dataStoreFirstPairProvider = { pairSettingsStore },
            shake2ReportApiProvider = { sentryApi }
        )
    }

    private val flipperLagsDetectorImpl: FlipperLagsDetector by lazy {
        FlipperLagsDetectorImpl(
            scopeProvider = { scope },
            serviceApiProvider = { flipperServiceApi },
            flipperActionNotifierProvider = { flipperActionNotifier }
        )
    }

    private val flipperRequestApiImpl by lazy {
        FlipperRequestApiImpl(
            scopeProvider = { scope },
            flipperActionNotifierProvider = { flipperActionNotifier },
            lagsDetectorProvider = { flipperLagsDetectorImpl },
            restartRPCApiProvider = { restartRPCApiImpl },
            sentryApiProvider = { sentryApi }
        )
    }

    private val flipperBleManagerImpl by lazy {
        FlipperBleManagerImpl(
            context = context,
            settingsStore = settingsStore,
            scope = scope,
            serviceErrorListener = serviceErrorListener,
            flipperActionNotifier = flipperActionNotifier,
            restartRPCApiProvider = { restartRPCApiImpl },
            informationApiProvider = { flipperInformationApiImpl },
            flipperRequestApiProvider = { flipperRequestApiImpl },
            flipperVersionApiProvider = { flipperVersionApiImpl },
            flipperReadyListenersProvider = { flipperReadyListeners }
        )
    }

    private val flipperServiceConnectDelegate by lazy {
        FlipperServiceConnectDelegate(
            bleManagerProvider = { flipperBleManagerImpl },
            contextProvider = { context },
            scannerProvider = { bluetoothScanner },
            adapterProvider = { bluetoothAdapter }
        )
    }

    private val flipperSafeConnectWrapper by lazy {
        FlipperSafeConnectWrapper(
            scopeProvider = { scope },
            serviceErrorListenerProvider = { serviceErrorListener },
            connectDelegateProvider = { flipperServiceConnectDelegate }
        )
    }

    private val flipperServiceApi by lazy {
        FlipperServiceApiImpl(
            scopeProvider = { scope },
            pairSettingsStoreProvider = { pairSettingsStore },
            bleManagerProvider = { flipperBleManagerImpl },
            flipperSafeConnectWrapperProvider = { flipperSafeConnectWrapper },
            unhandledExceptionApiProvider = { unhandledExceptionApi }
        )
    }

    override val serviceApiImpl: Provider<FlipperServiceApiImpl> = Provider {
        flipperServiceApi
    }
}
