package com.joydeep.flickrimagesearch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joydeep.flickrimagesearch.data.dao.ImageDao
import com.joydeep.flickrimagesearch.data.dao.RemoteKeyDao
import com.joydeep.flickrimagesearch.model.Image
import com.joydeep.flickrimagesearch.model.RemoteKey

@Database(
    entities = [Image::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    abstract fun getImageDao(): ImageDao
}