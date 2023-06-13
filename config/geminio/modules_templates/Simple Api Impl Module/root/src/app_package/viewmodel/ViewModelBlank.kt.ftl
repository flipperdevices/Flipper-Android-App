package ${packageName}.impl.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tangle.viewmodel.VMInject

class ${__formattedModuleName}ViewModel @VMInject constructor() : ViewModel() {

    private val state = MutableStateFlow<Unit>(Unit)
    fun getState() = state.asStateFlow()

}
