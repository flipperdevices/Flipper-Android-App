package com.flipperdevices.faphub.uninstallbutton.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import tangle.viewmodel.VMInject

class DeleteViewModel @VMInject constructor(
    private val queueApi: FapInstallationQueueApi
) : ViewModel() {
    fun onDelete(applicationUid: String) {
        queueApi.enqueue(FapActionRequest.Delete(applicationUid))
    }
}
