package com.dmariani.weathermvvm.di

import android.app.Application
import androidx.room.Room
import com.dmariani.weathermvvm.data.local.WeatherDao
import com.dmariani.weathermvvm.data.local.WeatherDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    private const val DATABASE_NAME = "weather.db"

    @Provides @Singleton
    fun provideDatabase(app: Application): WeatherDatabase {
        return Room.databaseBuilder(app, WeatherDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides @Singleton
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }
}