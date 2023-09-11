package com.pg.testgraph.core.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/api/test/points")
    suspend fun getPoints(@Query("count") count: Int): PointListResponse
}