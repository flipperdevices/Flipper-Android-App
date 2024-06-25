package com.flipperdevices.fmsearch.impl.viewmodel

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.fmsearch.impl.model.SearchItem
import com.flipperdevices.fmsearch.impl.model.SearchResult
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage.File.FileType
import com.flipperdevices.protobuf.storage.listRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class FMSearchViewModel @AssistedInject constructor(
    @Assisted private val startPath: String,
    serviceApiProvider: FlipperServiceProvider
) : DecomposeViewModel() {
    private val searchRequest = MutableStateFlow("")
    private val searchResponse = MutableStateFlow(
        SearchResult(inProgress = false, items = persistentListOf())
    )

    init {
        serviceApiProvider.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                searchRequest
                    .debounce(1.seconds)
                    .collectLatest { query ->
                        search(serviceApi, query)
                    }
            }
        }
    }

    fun search(query: String) {
        runBlocking { searchRequest.emit(query) }
    }

    fun getSearchResult() = searchResponse.asStateFlow()

    private suspend fun search(
        serviceApi: FlipperServiceApi,
        query: String
    ) = coroutineScope {
        if (query.isEmpty()) {
            return@coroutineScope
        }
        searchResponse.emit(SearchResult(inProgress = true, items = persistentListOf()))
        treeRecursive(
            scope = this,
            serviceApi = serviceApi,
            path = startPath
        ) { items ->
            val result = items.filter {
                it.name.contains(
                    query,
                    ignoreCase = true
                )
            }
            searchResponse.update {
                it.copy(items = (it.items + result).toImmutableList())
            }
        }
        searchResponse.update {
            it.copy(inProgress = false)
        }
    }

    private suspend fun treeRecursive(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        path: String,
        onReceiveListing: (List<SearchItem>) -> Unit,
    ) {
        val items = getListing(serviceApi, path)
        onReceiveListing(items)
        items.filter { it.isFolder }.map { folder ->
            scope.async(FlipperDispatchers.workStealingDispatcher) {
                treeRecursive(scope, serviceApi, folder.path, onReceiveListing)
            }
        }.forEach { it.await() }
    }

    private suspend fun getListing(
        serviceApi: FlipperServiceApi,
        requestPath: String
    ): List<SearchItem> {
        val response = serviceApi.requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = requestPath
                    includeMd5 = false
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).first()
        return response.storageListResponse.fileList.map {
            SearchItem(
                name = it.name,
                path = File(requestPath, it.name).absolutePath,
                isFolder = it.type == FileType.DIR
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            startPath: String
        ): FMSearchViewModel
    }
}
