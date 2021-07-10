package com.joydeep.flickrimagesearch.data.api

import com.joydeep.flickrimagesearch.model.ImageLinkResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {

    @GET("/services/rest/?method=flickr.photos.search")
    suspend fun getImageLinks(@QueryMap params: Map<String, String>): ImageLinkResponse
}