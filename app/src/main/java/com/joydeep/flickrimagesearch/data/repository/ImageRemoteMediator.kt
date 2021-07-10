package com.joydeep.flickrimagesearch.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.joydeep.flickrimagesearch.data.api.ApiService
import com.joydeep.flickrimagesearch.data.db.ImageDatabase
import com.joydeep.flickrimagesearch.model.PhotoEntity
import com.joydeep.flickrimagesearch.model.PhotoEntity2
import com.joydeep.flickrimagesearch.model.RemoteKey
import com.joydeep.flickrimagesearch.viewmodel.TEST
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String,
    private val service: ApiService,
    private val imageDatabase: ImageDatabase
) : RemoteMediator<Int, PhotoEntity2>() {

    companion object {
        private const val INITIAL_PAGE = 1
        const val API_KEY = "5cda947b931a0ade4161d0004589a7b0"
    }

    private val photoDao = imageDatabase.getPhoto2Dao()
    private val remoteDao = imageDatabase.getRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity2>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                Log.d(TEST, "refresh page called")
//                val remoteKey = getRemoteKeyForClosestItem(state)
//                remoteKey?.nextKey ?: INITIAL_PAGE
                INITIAL_PAGE
            }
            LoadType.APPEND -> {
                Log.d(TEST, "append page called")
                remoteDao.getRemoteKeyForQuery(query).nextKey
            }
            LoadType.PREPEND -> {
                Log.d(TEST, "prepend page called")

//                val remoteKey = getRemoteKeyForFirstItem(state)
//                remoteKey?.prevKey
//                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        Log.d(TEST, "${state.config.pageSize}")

        try {
            Log.d(TEST, "Inside try block")
            val map = mapOf(
                "api_key" to API_KEY,
                "text" to query,
                "per_page" to state.config.pageSize.toString(),
                "page" to page.toString(),
                "format" to "json",
                "nojsoncallback" to "1"
            )
            val response = service.getImageLinks(map)

            val photos = response.photos.photo
            val endOfPagination = photos.isEmpty()

            Log.d(TEST, "Inside try block2")


            imageDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photoDao.deletePhotos()
                    remoteDao.deleteRemoteKeys()
                }

                val lastQueryPosition = photoDao.getLastQueryPosition(query) ?: 0
                var queryPosition = lastQueryPosition + 1

                val images = photos.map { photo ->
                    val url =
                        "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
                    PhotoEntity2(
                        url = url,
                        searchQuery = query,
                        lastQueryPosition = queryPosition++
                    )
                }

//                val prevKey = if (page == INITIAL_PAGE) null else page - 1
                val nextKey = page + 1

//                val keys = images.map { photo ->
//                    RemoteKey(searchQuery = photo.searchQuery, prevKey = prevKey, nextKey = nextKey)
//                }

                val remoteKey = RemoteKey(searchQuery = query, prevKey = null, nextKey = nextKey)

                Log.d(TEST, "Inside try block4")

                photoDao.insertPhotos(images)
                remoteDao.insertRemoteKey(remoteKey)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (exception: IOException) {
            exception.printStackTrace()
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            exception.printStackTrace()

            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            exception.printStackTrace()

            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotoEntity>): RemoteKey? =
        state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { photo ->
            imageDatabase.getRemoteKeyDao().getRemoteKeyForQuery(photo.searchQuery)
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PhotoEntity>): RemoteKey? =
        state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { photo ->
            imageDatabase.getRemoteKeyDao().getRemoteKeyForQuery(photo.searchQuery)
        }

    private suspend fun getRemoteKeyForClosestItem(state: PagingState<Int, PhotoEntity>): RemoteKey? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.searchQuery?.let { searchQuery ->
                imageDatabase.getRemoteKeyDao().getRemoteKeyForQuery(searchQuery)
            }
        }
}