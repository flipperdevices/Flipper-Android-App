package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.core.ktx.jre.forEachIterable
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteViewModel @Inject constructor(
    private val deleteKeyApi: DeleteKeyApi
) : DecomposeViewModel() {
    fun onDeleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val deletedKeys = deleteKeyApi.getDeletedKeyAsFlow().first()
            deletedKeys.forEachIterable {
                deleteKeyApi.deleteMarkedDeleted(it.path)
            }
        }
    }

    fun onRestoreAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val deletedKeys = deleteKeyApi.getDeletedKeyAsFlow().first()
            deletedKeys.forEachIterable {
                deleteKeyApi.restore(it.path)
            }
        }
    }
}
