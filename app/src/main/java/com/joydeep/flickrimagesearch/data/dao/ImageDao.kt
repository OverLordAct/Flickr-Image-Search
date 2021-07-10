package com.joydeep.flickrimagesearch.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joydeep.flickrimagesearch.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<Image>)

    @Query("select * from image_table where searchQuery = :searchQuery order by lastQueryPosition")
    fun getPhotosByQuery(searchQuery: String): PagingSource<Int, Image>

    @Query("select max(lastQueryPosition) from image_table where searchQuery = :queryString")
    fun getLastQueryPosition(queryString: String): Int?

    @Query("select searchQuery from image_table where lastQueryPosition = (select max(lastQueryPosition) from image_table)")
    fun getLastQuery(): Flow<String>

    @Query("select searchQuery from image_table where lastQueryPosition = (select max(lastQueryPosition) from image_table)")
    suspend fun getLastQuery2(): String

    @Query("delete from image_table")
    suspend fun deletePhotos()
}