package com.dmariani.weathermvvm.domain.repository

import com.dmariani.weathermvvm.domain.model.Weather
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

/**
 * Single source of truth for weather data, abstracting over both remote (network/API)
 * and local (Room).
 */
interface WeatherRepository {
    fun getWeather(city: String, forceRefresh: Boolean = false): Single<Weather>
    fun observeRecentSearches(): Flowable<List<String>>
}