package com.joydeep.flickrimagesearch.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.joydeep.flickrimagesearch.data.api.ApiService
import com.joydeep.flickrimagesearch.data.db.ImageDatabase
import com.joydeep.flickrimagesearch.model.PhotoEntity
import com.joydeep.flickrimagesearch.model.PhotoEntity2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val apiService: ApiService,
    private val imageDatabase: ImageDatabase
) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    fun getImages(queryText: String): Flow<PagingData<PhotoEntity2>> {
        val pagingSource = {
            imageDatabase.getPhoto2Dao().getPhotosByQuery(queryText)
        }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                maxSize = 100
            ),
            pagingSourceFactory = pagingSource,
            remoteMediator = ImageRemoteMediator(queryText, apiService, imageDatabase)
        ).flow
    }

    suspend fun getPhotos(queryString: String): List<PhotoEntity> {
        val map = mutableMapOf<String, String>().apply {
            this["api_key"] = "5cda947b931a0ade4161d0004589a7b0"
            this["text"] = queryString
            this["per_page"] = "20"
            this["page"] = "1"
            this["format"] = "json"
            this["nojsoncallback"] = "1"
        }
        return apiService.getImageLinks(map).photos.photo.map { photo ->
            val url =
                "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
            PhotoEntity(photoId = photo.id, searchQuery = queryString, url = url)
        }
    }

    suspend fun getPhotosFlow(queryString: String): Flow<PhotoEntity> {
        val map = mutableMapOf<String, String>().apply {
            this["api_key"] = "5cda947b931a0ade4161d0004589a7b0"
            this["text"] = queryString
            this["per_page"] = "20"
            this["page"] = "1"
            this["format"] = "json"
            this["nojsoncallback"] = "1"
        }
        return apiService.getImageLinks(map).photos.photo.map { photo ->
            val url =
                "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
            PhotoEntity(photoId = photo.id, searchQuery = queryString, url = url)
        }.asFlow()
    }
}