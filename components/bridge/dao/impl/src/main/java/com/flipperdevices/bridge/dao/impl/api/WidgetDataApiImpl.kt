package com.flipperdevices.bridge.dao.impl.api

import androidx.room.withTransaction
import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.WidgetData
import com.flipperdevices.bridge.dao.api.model.WidgetType
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.ktx.getFlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.WidgetDataElement
import com.flipperdevices.bridge.dao.impl.repository.WidgetDataDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.withContext
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<WidgetDataApi>())
class WidgetDataApiImpl @Inject constructor(
    widgetDataDaoProvider: Provider<WidgetDataDao>,
    simpleKeyDaoProvider: Provider<SimpleKeyDao>,
    appDatabaseProvider: Provider<AppDatabase>
) : WidgetDataApi, LogTagProvider {
    override val TAG = "WidgetDataApi"

    private val widgetDataDao by widgetDataDaoProvider
    private val simpleKeyDao by simpleKeyDaoProvider
    private val appDatabase by appDatabaseProvider

    override suspend fun getAll(): List<WidgetData> = withContext(FlipperDispatchers.workStealingDispatcher) {
        return@withContext widgetDataDao.getAll().map { element ->
            val keyPath = if (element.keyId != null) {
                simpleKeyDao.getById(element.keyId)?.getFlipperKeyPath()
            } else {
                null
            }
            return@map WidgetData(element.id, keyPath, element.widgetType)
        }
    }

    override suspend fun getWidgetDataByWidgetId(
        widgetId: Int
    ): WidgetData? = withContext(FlipperDispatchers.workStealingDispatcher) {
        val widgetDataElement = widgetDataDao.getWidgetDataById(widgetId)
            ?: return@withContext null
        val keyPath = if (widgetDataElement.keyId != null) {
            simpleKeyDao.getById(widgetDataElement.keyId)?.getFlipperKeyPath()
        } else {
            null
        }
        return@withContext WidgetData(widgetDataElement.id, keyPath, widgetDataElement.widgetType)
    }

    override suspend fun updateTypeForWidget(
        widgetId: Int,
        type: WidgetType
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        appDatabase.withTransaction {
            val widgetData = widgetDataDao.getWidgetDataById(widgetId)

            if (widgetData == null) {
                widgetDataDao.insert(WidgetDataElement(id = widgetId, widgetType = type))
            } else {
                widgetDataDao.insert(widgetData.copy(widgetType = type))
            }
        }
    }

    override suspend fun deleteWidget(widgetId: Int) {
        widgetDataDao.delete(widgetId)
    }

    override suspend fun updateKeyForWidget(
        widgetId: Int,
        flipperKeyPath: FlipperKeyPath
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        appDatabase.withTransaction {
            val key = simpleKeyDao.getByPath(
                flipperKeyPath.path.pathToKey,
                flipperKeyPath.deleted
            ) ?: error("not found key for $flipperKeyPath")

            val widgetData = widgetDataDao.getWidgetDataById(widgetId)
            if (widgetData == null) {
                widgetDataDao.insert(WidgetDataElement(widgetId, key.uid))
            } else {
                widgetDataDao.insert(widgetData.copy(keyId = key.uid))
            }
        }
    }
}
