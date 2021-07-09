package com.joydeep.flickrimagesearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.joydeep.flickrimagesearch.data.repository.ImageRepository
import com.joydeep.flickrimagesearch.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private var currentQueryString: String? = null
    private var currentSearchResult: Flow<PagingData<Photo>>? = null

    fun getImages(queryString: String): Flow<PagingData<Photo>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryString && lastResult != null) return lastResult

        currentQueryString = queryString
        val newResult = repository.getImages(queryString)
        currentSearchResult = newResult
        return newResult
    }
}