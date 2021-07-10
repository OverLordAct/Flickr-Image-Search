package com.joydeep.flickrimagesearch.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.joydeep.flickrimagesearch.data.api.ApiService
import com.joydeep.flickrimagesearch.data.db.ImageDatabase
import com.joydeep.flickrimagesearch.model.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val apiService: ApiService,
    private val imageDatabase: ImageDatabase
) {

    companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_SIZE = 35
    }

    fun getImages(queryText: String, refreshOnInit: Boolean): Flow<PagingData<Image>> {
        val pagingSource = {
            imageDatabase.getImageDao().getPhotosByQuery(queryText)
        }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSource,
            remoteMediator = ImageRemoteMediator(queryText, apiService, imageDatabase, refreshOnInit)
        ).flow
    }

    fun getLastSearchQuery(): Flow<String> {
        return imageDatabase.getImageDao().getLastQuery()
    }

    suspend fun getLastQuery(): String {
        return imageDatabase.getImageDao().getLastQuery2()
    }
}