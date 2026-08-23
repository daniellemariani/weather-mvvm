package com.dmariani.weathermvvm.di

import android.app.Application
import com.dmariani.weathermvvm.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Root Dagger component for the app. Wires together Network, Database, Repository,
 * and ViewModel bindings into a single graph, and exposes field injection into
 * MainActivity. Built once in WeatherApp via its Factory.
 */
@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, RepositoryModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}