package com.flipperdevices.infrared.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.infrared.impl.api.EXTRA_KEY_PATH
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class InfraredViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val keyPath: FlipperKeyPath,
    keyStateHelperApi: KeyStateHelperApi.Builder,
) : ViewModel() {

    private val keyStateHelper = keyStateHelperApi.build(keyPath, viewModelScope)
    fun getState() = keyStateHelper.getKeyScreenState()

    fun setFavorite(isFavorite: Boolean) = keyStateHelper.setFavorite(isFavorite)

    fun onRename(onEndAction: (FlipperKeyPath) -> Unit) = keyStateHelper.onOpenEdit(onEndAction)

    fun onDelete(onEndAction: () -> Unit) = keyStateHelper.onDelete(onEndAction)
}
