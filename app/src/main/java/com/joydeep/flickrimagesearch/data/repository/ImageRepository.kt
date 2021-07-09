package com.joydeep.flickrimagesearch.data.repository

import androidx.paging.PagingData
import com.joydeep.flickrimagesearch.model.Photo
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(queryText: String): Flow<PagingData<Photo>>
}