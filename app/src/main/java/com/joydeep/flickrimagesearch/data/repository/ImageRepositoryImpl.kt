package com.joydeep.flickrimagesearch.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.joydeep.flickrimagesearch.data.api.ApiService
import com.joydeep.flickrimagesearch.data.db.ImageDatabase
import com.joydeep.flickrimagesearch.model.Photo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val imageDatabase: ImageDatabase
) : ImageRepository {

    companion object {
        private const val PAGE_SIZE = 10
    }

    override fun getImages(queryText: String): Flow<PagingData<Photo>> {
        val pagingSource = { imageDatabase.getPhotoDao().getImageById(queryText) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = pagingSource,
            remoteMediator = ImageRemoteMediator(queryText, apiService, imageDatabase)
        ).flow
    }
}