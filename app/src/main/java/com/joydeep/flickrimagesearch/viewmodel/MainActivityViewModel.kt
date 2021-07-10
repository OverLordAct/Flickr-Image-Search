package com.joydeep.flickrimagesearch.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.joydeep.flickrimagesearch.data.repository.ImageRepository
import com.joydeep.flickrimagesearch.model.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val LAST_SEARCH_QUERY: String = "LAST_SEARCH_QUERY"
    }

    private var refreshOnInit = false
    var scrollToTop = false

    var currentQueryString: MutableLiveData<String> =
        savedStateHandle.getLiveData(
            LAST_SEARCH_QUERY,
            repository.getLastSearchQuery().asLiveData().value
        )

    var currentSearchResult: Flow<PagingData<Image>> =
        currentQueryString.asFlow().flatMapLatest { query ->
            query?.let {
                repository.getImages(query, refreshOnInit)
            } ?: emptyFlow()
        }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val lastQuery = repository.getLastQuery()
            currentQueryString.postValue(lastQuery)
        }
    }

    fun getImages(queryString: String) {
        if (queryString == currentQueryString.value) return
        refreshOnInit = true
        savedStateHandle[LAST_SEARCH_QUERY] = queryString
        currentQueryString.value = queryString
        scrollToTop = true
    }
}