package com.joydeep.flickrimagesearch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joydeep.flickrimagesearch.data.dao.Photo2Dao
import com.joydeep.flickrimagesearch.data.dao.PhotoDao
import com.joydeep.flickrimagesearch.data.dao.RemoteKeyDao
import com.joydeep.flickrimagesearch.model.PhotoEntity
import com.joydeep.flickrimagesearch.model.PhotoEntity2
import com.joydeep.flickrimagesearch.model.RemoteKey

@Database(
    entities = [PhotoEntity::class, RemoteKey::class, PhotoEntity2::class],
    version = 1,
    exportSchema = false
)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun getPhotoDao(): PhotoDao

    abstract fun getRemoteKeyDao(): RemoteKeyDao

    abstract fun getPhoto2Dao(): Photo2Dao
}