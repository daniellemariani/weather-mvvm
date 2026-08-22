package com.dmariani.weathermvvm.data.repository

import com.dmariani.weathermvvm.data.local.WeatherDao
import com.dmariani.weathermvvm.data.remote.WeatherApi
import com.dmariani.weathermvvm.data.toDomain
import com.dmariani.weathermvvm.data.toEntity
import com.dmariani.weathermvvm.domain.model.City
import com.dmariani.weathermvvm.domain.model.Weather
import com.dmariani.weathermvvm.domain.repository.WeatherRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
): WeatherRepository {

    companion object {
        private const val STALE_THRESHOLD_MS = 10 * 60 * 1000
    }

    override fun getWeather(city: City, forceRefresh: Boolean): Single<Weather> {
        if (forceRefresh) {
            return fetchAndCacheWeather(city)
        }

        // check cached data
        return dao.getOnce(city.name)
            .flatMapSingle { entity ->
                // call if cached data exist
                val now = System.currentTimeMillis()
                if (now - entity.cachedAt <= STALE_THRESHOLD_MS) {
                    // cached data is valid, no need to refresh
                    Single.just(entity.toDomain())
                } else {
                    // cached data is old, refresh
                    fetchAndCacheWeather(city)
                }
            }
            .switchIfEmpty(fetchAndCacheWeather(city)) // call if cache request is empty
    }

    override fun observeRecentSearches(limit: Int): Flowable<List<String>> {
        return dao.recentCities()
    }

    private fun fetchAndCacheWeather(city: City): Single<Weather> {
        // fetch city weather from API
        return api.getWeather(city.lat, city.lon)
            .flatMap { response ->
                // successful request, insert/update city in the database
                val entity = response.toEntity(city.name, System.currentTimeMillis())
                dao.insert(entity)
                    .andThen(Single.just(response.toDomain(city.name)))
            }.onErrorResumeNext { error ->
                // network failed, check if a cache entry exists regardless of its timestamp
                dao.getOnce(city.name)
                    .flatMapSingle { entity ->
                        // cache exist, return value
                        Single.just(entity.toDomain())
                    }
                    .switchIfEmpty(Single.error(error)) // cache doesn't exist, return error
            }
    }


}