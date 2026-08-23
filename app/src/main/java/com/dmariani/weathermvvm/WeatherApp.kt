package com.dmariani.weathermvvm

import android.app.Application
import com.dmariani.weathermvvm.di.AppComponent
import com.dmariani.weathermvvm.di.DaggerAppComponent

class WeatherApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}