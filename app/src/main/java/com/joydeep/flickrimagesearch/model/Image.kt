package com.joydeep.flickrimagesearch.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "image_table", primaryKeys = ["searchQuery", "url"])
data class Image(
    @SerializedName("searchQuery") val searchQuery: String,
    @SerializedName("url") val url: String,
    @SerializedName("lastQueryPosition") val lastQueryPosition: Int
)
