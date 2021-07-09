package com.joydeep.flickrimagesearch.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.joydeep.flickrimagesearch.data.api.ApiService
import com.joydeep.flickrimagesearch.data.db.ImageDatabase
import com.joydeep.flickrimagesearch.model.Photo
import com.joydeep.flickrimagesearch.model.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String,
    private val service: ApiService,
    private val imageDatabase: ImageDatabase
) : RemoteMediator<Int, Photo>() {

    companion object {
        private const val INITIAL_PAGE = 1
        private const val API_KEY = "5cda947b931a0ade4161d0004589a7b0"
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Photo>
    ): RemoteMediator.MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                // TODO: 09-07-2021 Check how page keys are handled
                val remoteKey = getRemoteKeyForClosestItem(state)
                remoteKey?.nextKey?.minus(1) ?: INITIAL_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                remoteKey?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                remoteKey?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }
        }

        // TODO: 09-07-2021 Build query param here
        // https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=5cda947b931a0ade4161d0004589a7b0&text=cats&per_page=10&page=1&format=json&nojsoncallback=1

        val map = mapOf(
            "api_key" to API_KEY,
            "text" to query,
            "per_page" to state.config.pageSize.toString(),
            "page" to page.toString(),
            "format" to "json",
            "nojsoncallback" to "1"
        )
        try {
            val response = service.getImageLinks(map)
            val endOfPagination = response.photos.photo.isEmpty()

            val images = response.photos.photo.map { photo ->
                // TODO: 09-07-2021 Create url here
                // https://live.staticflickr.com/{server-id}/{id}_{secret}_{size-suffix}.jpg
                val url =
                    "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
                Photo(
                    id = photo.id,
                    url = url,
                    query = query
                )
            }

            imageDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    imageDatabase.getPhotoDao().clearImages()
                    imageDatabase.getRemoteKeyDao().clearRemoteKeys()
                }

                val prevKey = if (page == INITIAL_PAGE) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                val keys = images.map { photo ->
                    RemoteKey(photoId = photo.id, prevKey = prevKey, nextKey = nextKey)
                }

                imageDatabase.getPhotoDao().insertPhoto(images)
                imageDatabase.getRemoteKeyDao().insertAll(keys)
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

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Photo>): RemoteKey? =
        state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { photo ->
            imageDatabase.getRemoteKeyDao().getRemoteKeyForId(photo.id)
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Photo>): RemoteKey? =
        state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { photo ->
            imageDatabase.getRemoteKeyDao().getRemoteKeyForId(photo.id)
        }

    private suspend fun getRemoteKeyForClosestItem(state: PagingState<Int, Photo>): RemoteKey? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                imageDatabase.getRemoteKeyDao().getRemoteKeyForId(id)
            }
        }
}