package com.joydeep.flickrimagesearch.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joydeep.flickrimagesearch.model.PhotoEntity

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photoEntities: List<PhotoEntity>)

    @Query("delete from photo_master")
    suspend fun clearImages()

    @Query("select * from photo_master where searchQuery = :queryString")
    fun getImageByQuery(queryString: String): PagingSource<Int, PhotoEntity>
}