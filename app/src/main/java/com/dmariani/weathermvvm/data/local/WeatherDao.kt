package com.dmariani.weathermvvm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface WeatherDao {

    @Query("SELECT * FROM  weather WHERE city = :city")
    fun observeWeather(city: String): Flowable<WeatherEntity>

    @Query("SELECT * FROM weather WHERE city = :city")
    fun getOnce(city: String): Maybe<WeatherEntity>

    @Query("SELECT city FROM weather ORDER BY cachedAt DESC LIMIT :limit")
    fun recentCities(limit: Int = 5): Flowable<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: WeatherEntity): Completable
}