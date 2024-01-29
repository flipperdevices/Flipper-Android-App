package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.flipperdevices.bridge.dao.impl.model.FlipperAdditionalFile

@Dao
interface AdditionalFileDao {
    @Query(
        "SELECT flipper_files.* FROM flipper_files JOIN keys ON flipper_files.key_id == keys.uid " +
            "WHERE flipper_files.path = :path AND keys.deleted = :keyDeleted"
    )
    suspend fun getByPath(path: String, keyDeleted: Boolean): FlipperAdditionalFile?

    @Delete
    suspend fun delete(file: FlipperAdditionalFile)

    @Update
    suspend fun update(file: FlipperAdditionalFile)

    @Insert
    suspend fun insert(file: FlipperAdditionalFile)

    @Query("SELECT * FROM flipper_files WHERE key_id = :keyId")
    suspend fun getFilesForKeyWithId(keyId: Int): List<FlipperAdditionalFile>
}
