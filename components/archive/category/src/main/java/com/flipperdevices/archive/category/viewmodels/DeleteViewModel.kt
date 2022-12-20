package com.flipperdevices.archive.category.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.category.di.CategoryComponent
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.forEachIterable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteViewModel : ViewModel() {
    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    init {
        ComponentHolder.component<CategoryComponent>().inject(this)
    }

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
