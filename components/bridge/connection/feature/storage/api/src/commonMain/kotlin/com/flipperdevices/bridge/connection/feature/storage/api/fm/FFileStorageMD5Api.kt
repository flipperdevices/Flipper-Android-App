package com.flipperdevices.bridge.connection.feature.storage.api.fm

interface FFileStorageMD5Api {
    /**
     * @return MD5 of the file or return an error inside the result
     */
    suspend fun md5(path: String): Result<String>
}
