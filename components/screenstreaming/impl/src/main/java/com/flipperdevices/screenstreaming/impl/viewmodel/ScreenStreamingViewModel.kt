package com.flipperdevices.screenstreaming.impl.viewmodel

import android.app.Application
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.ButtonStackRepository
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.FlipperButtonRepository
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.LockRepository
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.StreamingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val VIBRATOR_TIME_MS = 10L

class ScreenStreamingViewModel @AssistedInject constructor(
    @Assisted private val lifecycleOwner: LifecycleOwner,
    serviceProvider: FlipperServiceProvider,
    application: Application,
    private val flipperButtonRepository: FlipperButtonRepository,
    private val buttonStackRepository: ButtonStackRepository,
    private val settings: DataStore<Settings>
) : DecomposeViewModel() {
    private val vibrator = ContextCompat.getSystemService(application, Vibrator::class.java)

    private val lockRepository = LockRepository(
        scope = viewModelScope,
        stackRepository = buttonStackRepository,
        serviceProvider = serviceProvider
    )
    private val streamingRepository = StreamingRepository(viewModelScope)

    init {
        serviceProvider.provideServiceApi(lockRepository, this)
        serviceProvider.provideServiceApi(streamingRepository, this)
        lifecycleOwner.lifecycle.subscribe(streamingRepository)
    }

    fun getFlipperScreen(): StateFlow<FlipperScreenState> = streamingRepository.getFlipperScreen()
    fun getFlipperButtons() = buttonStackRepository.getButtonStack()
    fun getLockState() = lockRepository.getLockState()
    fun onChangeLock(isWillBeLocked: Boolean) = lockRepository.onChangeLock(isWillBeLocked)
    fun onPressButton(
        buttonEnum: ButtonEnum,
        inputType: Gui.InputType
    ) {
        vibrator?.vibrateCompat(
            VIBRATOR_TIME_MS,
            runBlocking { settings.data.first().disabled_vibration }
        )

        val uuid = buttonStackRepository.onNewStackButton(buttonEnum.animEnum)
        flipperButtonRepository.pressOnButton(
            viewModelScope = viewModelScope,
            key = buttonEnum.key,
            type = inputType,
            onComplete = {
                buttonStackRepository.onRemoveStackButton(uuid)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleOwner.lifecycle.unsubscribe(streamingRepository)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            lifecycleOwner: LifecycleOwner
        ): ScreenStreamingViewModel
    }
}
