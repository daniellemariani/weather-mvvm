package com.dmariani.weathermvvm.di

import android.app.Application
import com.dmariani.weathermvvm.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, RepositoryModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}