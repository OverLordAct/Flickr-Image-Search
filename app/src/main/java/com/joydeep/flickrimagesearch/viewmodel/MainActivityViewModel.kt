package com.joydeep.flickrimagesearch.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.joydeep.flickrimagesearch.data.repository.ImageRepository
import com.joydeep.flickrimagesearch.model.PhotoEntity
import com.joydeep.flickrimagesearch.model.PhotoEntity2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TEST = "TESTING"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private var currentQueryString = MutableLiveData<String>()
    var currentSearchResult: Flow<PagingData<PhotoEntity2>> =
        currentQueryString.asFlow().flatMapLatest { query ->
            repository.getImages(query)
        }.cachedIn(viewModelScope)

    fun getImages(queryString: String) {
        Log.d(TEST, "getImages called in viewModel")
        if (queryString == currentQueryString.value) return

        currentQueryString.value = queryString
//        currentQueryString = queryString
//        val newResult = repository.getImages(queryString).cachedIn(viewModelScope)
//        currentSearchResult = newResult
//        Log.d(TEST, "getImages $currentSearchResult")
//        return newResult


    }

    var photosLiveData = MutableLiveData<List<PhotoEntity>>()

    fun getPhotos(queryString: String) {
        viewModelScope.launch {
            photosLiveData.value = repository.getPhotos(queryString)
        }
    }

    private var currentQuery = MutableLiveData<String>()

//    @ExperimentalCoroutinesApi
//    val searchResults = currentQuery.asFlow().flatMapLatest { query ->
//        query?.let {
//            repository.getPhotosFlow(query)
//        } ?: emptyFlow()
//    }.asLiveData()

    val searchResults = currentQuery.asFlow().flatMapLatest { query ->
        query?.let {
            repository.getPhotosFlow(query)
        } ?: emptyFlow()
    }

    fun getPhotosFlow(queryString: String) {
        currentQuery.value = queryString
    }
}