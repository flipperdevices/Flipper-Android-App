package com.flipperdevices.faphub.screenshotspreview.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageSelectViewModel @Inject constructor() : DecomposeViewModel() {
    private val eventChannel = Channel<Event>()
    val eventFlow: Flow<Event> = eventChannel.receiveAsFlow()

    fun onImageSelected(index: Int) {
        viewModelScope.launch {
            val event = Event.ImageSelected(index)
            eventChannel.send(event)
        }
    }

    sealed interface Event {
        class ImageSelected(val index: Int) : Event
    }
}
