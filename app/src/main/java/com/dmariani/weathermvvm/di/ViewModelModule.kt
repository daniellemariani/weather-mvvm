package com.dmariani.weathermvvm.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

/**
 * I'm declaring a new annotation, usable only on functions, whose value should be visible
 * to reflection at runtime, and which Dagger should treat as a valid map key type.
 *
 * This custom annotation is the "key" for ViewModel map entry,
 * so Dagger knows CustomViewModel::class maps to a particular binding.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindWeatherViewModel(vm: WeatherViewModel): ViewModel
}