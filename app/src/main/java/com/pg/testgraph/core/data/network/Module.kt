package com.pg.testgraph.core.data.network

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { PointsRepository(get()) }
    single {
        Retrofit.Builder()
            .baseUrl("https://hr-challenge.interactivestandard.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(ApiService::class.java) }
}