package com.joydeep.flickrimagesearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val searchQuery: String,
    val prevKey: Int?,
    val nextKey: Int
)
