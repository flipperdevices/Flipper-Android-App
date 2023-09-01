package com.flipperdevices.uploader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.share.api.CryptoStorageApi

class UploaderViewModelFactory(
    private val keyParser: KeyParser,
    private val cryptoStorageApi: CryptoStorageApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
    private val flipperKeyPath: FlipperKeyPath
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UploaderViewModel(
            keyParser = keyParser,
            cryptoStorageApi = cryptoStorageApi,
            simpleKeyApi = simpleKeyApi,
            metricApi = metricApi,
            flipperKeyPath = flipperKeyPath
        ) as T
    }
}
