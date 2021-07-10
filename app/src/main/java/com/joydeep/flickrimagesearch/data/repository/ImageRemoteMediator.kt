package com.joydeep.flickrimagesearch.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.joydeep.flickrimagesearch.data.api.ApiService
import com.joydeep.flickrimagesearch.data.api.ApiService.Companion.API_KEY
import com.joydeep.flickrimagesearch.data.db.ImageDatabase
import com.joydeep.flickrimagesearch.model.Image
import com.joydeep.flickrimagesearch.model.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String,
    private val service: ApiService,
    private val imageDatabase: ImageDatabase,
    private val refreshOnInit: Boolean
) : RemoteMediator<Int, Image>() {

    companion object {
        private const val INITIAL_PAGE = 1
    }

    private val imageDao = imageDatabase.getImageDao()
    private val remoteDao = imageDatabase.getRemoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        return if (refreshOnInit) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Image>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE
            LoadType.APPEND -> remoteDao.getRemoteKeyForQuery(query).nextKey
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
        }

        val queryParams =
            constructQueryParams(state.config.pageSize.toString(), page.toString())

        try {
            val response = service.getImageLinks(queryParams)

            val imagesServer = response.photos.photo
            val endOfPagination = imagesServer.isEmpty()

            imageDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    imageDao.deletePhotos()
                    remoteDao.deleteRemoteKeys()
                }

                val lastQueryPosition = imageDao.getLastQueryPosition(query) ?: 0
                var queryPosition = lastQueryPosition + 1

                val images = imagesServer.map { photo ->
                    val url =
                        "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
                    Image(
                        url = url,
                        searchQuery = query,
                        lastQueryPosition = queryPosition++
                    )
                }

                val nextKey = page.plus(1)

                val remoteKey = RemoteKey(searchQuery = query, nextKey = nextKey)

                imageDao.insertPhotos(images)
                remoteDao.insertRemoteKey(remoteKey)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private fun constructQueryParams(perPage: String, page: String): Map<String, String> {
        return mapOf(
            "api_key" to API_KEY,
            "text" to query,
            "per_page" to perPage,
            "page" to page,
            "format" to "json",
            "nojsoncallback" to "1"
        )
    }
}