package com.dmariani.weathermvvm.di

import com.dmariani.weathermvvm.data.remote.WeatherApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Provides the Retrofit instance (Open-Meteo, Gson conversion, RxJava3 call adapter)
 * and the WeatherApi built from it. All network-related bindings live here.
 */
@Module
object NetworkModule {

    private const val BASE_URL = "https://api.open-meteo.com/"

    @Provides @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
}