package com.joydeep.flickrimagesearch.data.api

import com.joydeep.flickrimagesearch.model.ImageLinkResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {
    companion object {
        const val API_KEY = "5cda947b931a0ade4161d0004589a7b0"
    }

    @GET("/services/rest/?method=flickr.photos.search")
    suspend fun getImageLinks(@QueryMap params: Map<String, String>): ImageLinkResponse
}