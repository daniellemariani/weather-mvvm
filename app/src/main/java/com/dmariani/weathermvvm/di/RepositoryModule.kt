package com.dmariani.weathermvvm.di

import com.dmariani.weathermvvm.data.repository.WeatherRepositoryImpl
import com.dmariani.weathermvvm.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindWeatherRepository(repository: WeatherRepositoryImpl): WeatherRepository
}