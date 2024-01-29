package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.UtilsKeyDao

/**
 * By default all method exclude deleted field
 */
@Dao
interface KeyDao : DeleteKeyDao, SimpleKeyDao, UtilsKeyDao
