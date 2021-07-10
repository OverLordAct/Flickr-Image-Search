package com.joydeep.flickrimagesearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photo_master")
data class PhotoEntity(
    @PrimaryKey @SerializedName("photoId") val photoId: Long,
    @SerializedName("searchQuery") val searchQuery: String,
    @SerializedName("url") val url: String
)

@Entity(tableName = "photo_entity_2", primaryKeys = ["searchQuery", "url"])
data class PhotoEntity2(
    val searchQuery: String,
    val url: String,
    val lastQueryPosition: Int
)
