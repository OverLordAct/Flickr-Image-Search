package com.joydeep.flickrimagesearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val photoId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
