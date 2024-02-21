package com.flipperdevices.faphub.dao.network.helper

import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitApplicationApi
import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitApplicationShort
import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitException
import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitExceptionCode
import com.flipperdevices.faphub.dao.network.ktorfit.model.requests.KtorfitApplicationApiRequest
import com.flipperdevices.faphub.dao.network.ktorfit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.ktorfit.model.types.SortOrderType
import com.flipperdevices.faphub.errors.api.throwable.FirmwareNotSupported
import com.flipperdevices.faphub.target.model.FlipperTarget
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import javax.inject.Inject

private const val MAX_QUERY_ARRAY_SIZE = 500

@Suppress("LongParameterList")
class FapApplicationReceiveHelper @Inject constructor(
    private val applicationApi: KtorfitApplicationApi,
) : LogTagProvider {
    override val TAG = "FapApplicationReceiveHelper"
    suspend fun get(
        target: FlipperTarget,
        category: FapCategory?,
        sortType: SortType,
        offset: Int,
        limit: Int,
        applicationIds: List<String>?
    ): List<KtorfitApplicationShort> {
        if (limit - offset > MAX_QUERY_ARRAY_SIZE) {
            warn { "Limit larger then $MAX_QUERY_ARRAY_SIZE. Limit is: $limit and offset is $offset" }
        }
        return try {
            getUnsafe(
                target = target,
                category = category,
                sortType = sortType,
                offset = offset,
                limit = limit,
                applicationIds = applicationIds
            )
        } catch (requestException: ClientRequestException) {
            val ktorfitException = runCatching {
                requestException.response.body<KtorfitException>()
            }.getOrNull() ?: throw requestException
            val exceptionCode = KtorfitExceptionCode.fromCode(ktorfitException.detail.code)
            throw if (exceptionCode == KtorfitExceptionCode.UNKNOWN_SDK) {
                FirmwareNotSupported(requestException)
            } else {
                requestException
            }
        }
    }

    private suspend fun getUnsafe(
        target: FlipperTarget,
        category: FapCategory?,
        sortType: SortType,
        offset: Int,
        limit: Int,
        applicationIds: List<String>?
    ): List<KtorfitApplicationShort> {
        @Suppress("IfThenToElvis")
        return when (target) {
            FlipperTarget.Unsupported -> throw FirmwareNotSupported()
            FlipperTarget.NotConnected -> if (applicationIds == null) {
                applicationApi.getAll(
                    KtorfitApplicationApiRequest(
                        offset = offset,
                        limit = limit,
                        sortBy = ApplicationSortType.fromSortType(sortType),
                        sortOrder = SortOrderType.fromSortType(sortType),
                        categoryId = category?.id
                    )
                )
            } else {
                applicationIds.chunked(MAX_QUERY_ARRAY_SIZE).pmap {
                    applicationApi.getAll(
                        KtorfitApplicationApiRequest(
                            offset = 0,
                            limit = it.size,
                            sortBy = ApplicationSortType.fromSortType(sortType),
                            sortOrder = SortOrderType.fromSortType(sortType),
                            categoryId = category?.id,
                            applications = it
                        )
                    )
                }.flatten()
            }

            is FlipperTarget.Received -> if (applicationIds == null) {
                applicationApi.getAll(
                    KtorfitApplicationApiRequest(
                        offset = offset,
                        limit = limit,
                        sortBy = ApplicationSortType.fromSortType(sortType),
                        sortOrder = SortOrderType.fromSortType(sortType),
                        categoryId = category?.id,
                        target = target.target,
                        sdkApiVersion = target.sdk.toString(),
                    )
                )
            } else {
                applicationIds.chunked(MAX_QUERY_ARRAY_SIZE).pmap {
                    applicationApi.getAll(
                        KtorfitApplicationApiRequest(
                            offset = 0,
                            limit = it.size,
                            sortBy = ApplicationSortType.fromSortType(sortType),
                            sortOrder = SortOrderType.fromSortType(sortType),
                            categoryId = category?.id,
                            applications = it,
                            target = target.target,
                            sdkApiVersion = target.sdk.toString(),
                        )
                    )
                }.flatten()
            }
        }
    }
}
