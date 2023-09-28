package com.pg.testgraph.core.data.network

import android.util.Log
import com.pg.testgraph.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val ioDispatcherName = "IoDispatcher"
const val mainDispatcherName = "MainDispatcher"
const val exceptionHandlerName = "ExceptionHandler"

val networkModule = module {
    single { PointsRepository(get(), get(named(ioDispatcherName))) }
    single {
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(logging)
            }
        }
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://hr-challenge.interactivestandard.com")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(ApiService::class.java) }
}

val dispatcherModule = module {
    single(named(ioDispatcherName)) { Dispatchers.IO }
    single(named(mainDispatcherName)) { Dispatchers.Main }
    single(named(exceptionHandlerName)) {
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("CoroutineExceptionHandler", "$coroutineContext", throwable)
        }
    }
}