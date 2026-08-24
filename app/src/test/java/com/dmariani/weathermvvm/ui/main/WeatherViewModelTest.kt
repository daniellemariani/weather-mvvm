package com.dmariani.weathermvvm.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dmariani.weathermvvm.data.repository.FakeWeatherRepository
import com.dmariani.weathermvvm.domain.model.City
import com.dmariani.weathermvvm.domain.model.Weather
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        // Force RxJava/RxAndroid schedulers to run synchronously in tests
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        fakeRepository = FakeWeatherRepository()
        viewModel = WeatherViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `onCitySelected success updates weather LiveData`() {
        val city = City(name = "Los Angeles", lat = 34.0522, lon = -118.2437)
        val weather = Weather(city = "Los Angeles", temperature = 32.0, condition = "Clear", isDay = true)
        fakeRepository.forcedWeather = weather
        viewModel.onCitySelected(city)
        assertEquals(weather, viewModel.weather.value)
    }

    @Test
    fun `onCitySelected failure updates errorMessage and snackbarEvent`() {
        val city = City(name = "Los Angeles", lat = 34.0522, lon = -118.2437)
        fakeRepository.forcedError = RuntimeException("Network Error")

        viewModel.onCitySelected(city)

        assertEquals("Unable to load weather from Los Angeles", viewModel.errorMessage.value)
        assertEquals("Unable to load weather from Los Angeles", viewModel.snackbarEvent.value?.getContentIfNotHandled())

    }
}