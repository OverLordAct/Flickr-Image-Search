package com.joydeep.flickrimagesearch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joydeep.flickrimagesearch.data.dao.PhotoDao
import com.joydeep.flickrimagesearch.data.dao.RemoteKeyDao
import com.joydeep.flickrimagesearch.model.Photo
import com.joydeep.flickrimagesearch.model.RemoteKey

@Database(entities = [Photo::class, RemoteKey::class], version = 1, exportSchema = false)
abstract class ImageDatabase: RoomDatabase() {
    abstract fun getPhotoDao(): PhotoDao

    abstract fun getRemoteKeyDao(): RemoteKeyDao
}