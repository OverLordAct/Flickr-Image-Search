package com.joydeep.flickrimagesearch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joydeep.flickrimagesearch.model.RemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKey(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys_table where searchQuery = :queryString")
    suspend fun getRemoteKeyForQuery(queryString: String): RemoteKey

    @Query("DELETE FROM remote_keys_table")
    suspend fun deleteRemoteKeys()
}