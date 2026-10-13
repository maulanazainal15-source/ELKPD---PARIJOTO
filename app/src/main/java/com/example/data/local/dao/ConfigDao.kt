package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: ConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfigs(configs: List<ConfigEntity>)

    @Query("SELECT value FROM app_config WHERE `key` = :key LIMIT 1")
    suspend fun getConfigValue(key: String): String?

    @Query("SELECT * FROM app_config")
    fun observeAllConfigs(): Flow<List<ConfigEntity>>

    @Query("SELECT * FROM app_config")
    suspend fun getAllConfigs(): List<ConfigEntity>
}

