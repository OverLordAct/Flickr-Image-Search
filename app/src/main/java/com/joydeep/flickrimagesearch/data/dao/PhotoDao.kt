package com.joydeep.flickrimagesearch.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joydeep.flickrimagesearch.model.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photos: List<Photo>)

    @Query("delete from photo_master")
    suspend fun clearImages()

    @Query("select * from photo_master where `query` like :query order by id asc")
    fun getImageById(query: String): PagingSource<Int, Photo>
}