package com.dmariani.weathermvvm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val city: String,
    val temperature: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val cachedAt: Long
)