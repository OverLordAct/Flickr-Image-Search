package com.joydeep.flickrimagesearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "remote_keys_table")
data class RemoteKey(
    @PrimaryKey @SerializedName("searchQuery") val searchQuery: String,
    @SerializedName("nextKey") val nextKey: Int
)
