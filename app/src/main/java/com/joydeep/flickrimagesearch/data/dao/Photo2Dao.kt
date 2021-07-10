package com.joydeep.flickrimagesearch.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joydeep.flickrimagesearch.model.PhotoEntity2

@Dao
interface Photo2Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity2>)

    @Query("select * from photo_entity_2 where searchQuery = :searchQuery order by lastQueryPosition")
    fun getPhotosByQuery(searchQuery: String): PagingSource<Int, PhotoEntity2>

    @Query("select max(lastQueryPosition) from photo_entity_2 where searchQuery = :queryString")
    fun getLastQueryPosition(queryString: String): Int?

    @Query("delete from photo_entity_2")
    suspend fun deletePhotos()
}