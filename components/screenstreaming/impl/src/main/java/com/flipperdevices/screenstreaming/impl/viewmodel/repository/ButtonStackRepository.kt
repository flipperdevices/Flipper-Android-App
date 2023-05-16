package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.flipperdevices.screenstreaming.impl.model.ButtonAnimEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperButtonStackElement
import java.util.UUID
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ButtonStackRepository @Inject constructor() {
    private val stackFlipperButtons =
        MutableStateFlow(persistentListOf<FlipperButtonStackElement>())

    fun getButtonStack() = stackFlipperButtons.asStateFlow()

    fun onNewStackButton(button: ButtonAnimEnum): UUID {
        val stackElement = FlipperButtonStackElement(button)
        stackFlipperButtons.update {
            it.add(stackElement)
        }
        return stackElement.uuid
    }

    fun onRemoveStackButton(uuid: UUID) {
        stackFlipperButtons.update { stack ->
            stack.removeAll { it.uuid == uuid }
        }
    }
}