package com.flipperdevices.screenstreaming.impl.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.ButtonStackRepository
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.FlipperButtonRepository
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.LockRepository
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.StreamingRepository
import kotlinx.coroutines.flow.StateFlow
import tangle.viewmodel.VMInject

class ScreenStreamingViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    application: Application,
    private val flipperButtonRepository: FlipperButtonRepository,
    private val buttonStackRepository: ButtonStackRepository,
) : AndroidLifecycleViewModel(application) {

    private val lockRepository = LockRepository(
        scope = viewModelScope,
        stackRepository = buttonStackRepository,
        serviceProvider = serviceProvider
    )
    private val streamingRepository = StreamingRepository(viewModelScope)

    init {
        serviceProvider.provideServiceApi(lockRepository, this)
        serviceProvider.provideServiceApi(streamingRepository, this)
    }

    fun getFlipperScreen(): StateFlow<FlipperScreenState> = streamingRepository.getFlipperScreen()
    fun getFlipperButtons() = buttonStackRepository.getButtonStack()
    fun getLockState() = lockRepository.getLockState()
    fun onChangeLock(isWillBeLocked: Boolean) = lockRepository.onChangeLock(isWillBeLocked)
    fun enableStreaming() = streamingRepository.enableStreaming()
    fun disableStreaming() = streamingRepository.disableStreaming()
    fun onPressButton(
        buttonEnum: ButtonEnum,
        inputType: Gui.InputType
    ) {
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
}
