package com.joydeep.flickrimagesearch.data.api

import com.joydeep.flickrimagesearch.model.ImageLinkResponse
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

interface ApiService {

    // https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=5cda947b931a0ade4161d0004589a7b0&text=cats&per_page=10&page=1&format=json&nojsoncallback=1

    @GET("/services/rest/?method=flickr.photos.search&api_key=5cda947b931a0ade4161d0004589a7b0&text=cats&per_page=10&page=1&format=json&nojsoncallback=1")
    @FormUrlEncoded
    suspend fun getImageLinks(@FieldMap params: Map<String, String>): ImageLinkResponse

    // https://live.staticflickr.com/{server-id}/{id}_{secret}_{size-suffix}.jpg

//    @GET("/{server-id}/{id}_{secret}_{size-suffix}.jpg")
//    suspend fun getImage
}