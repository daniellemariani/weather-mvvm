package com.dmariani.weathermvvm.data.repository

import com.dmariani.weathermvvm.domain.model.City
import com.dmariani.weathermvvm.domain.model.Weather
import com.dmariani.weathermvvm.domain.repository.WeatherRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class FakeWeatherRepository : WeatherRepository {

    var forcedWeather: Weather? = null
    var forcedError: Throwable? = null

    override fun getWeather(city: City, forceRefresh: Boolean): Single<Weather> {
        return forcedError?.let { Single.error(it) }
            ?: forcedWeather?.let { Single.just(it) }
            ?: Single.error(IllegalStateException("No forced state configured"))
    }

    override fun observeRecentSearches(limit: Int): Flowable<List<String>> {
        return Flowable.just(emptyList())
    }
}