package com.joydeep.flickrimagesearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photo_master")
data class Photo(
    @PrimaryKey @SerializedName("id") val id: Long,
    @SerializedName("query") val query: String,
    @SerializedName("url") val url: String
)
