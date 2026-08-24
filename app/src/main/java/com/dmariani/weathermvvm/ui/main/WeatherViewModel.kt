package com.dmariani.weathermvvm.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmariani.weathermvvm.domain.model.City
import com.dmariani.weathermvvm.domain.model.Weather
import com.dmariani.weathermvvm.domain.repository.WeatherRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Exposes weather state as independent LiveData properties. Persistent state
 * (weather, error) is separated from the one-time snackbarEvent, which uses
 * Event<T> to avoid re-firing on configuration changes.
 */
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private var lastCity: City? = null
    val currentCity: City? get() = lastCity

    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> get() = _weather

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _recentSearches = MutableLiveData<List<String>>()
    val recentSearches: LiveData<List<String>> get() = _recentSearches

    private val _snackbarEvent = MutableLiveData<Event<String>>()
    val snackbarEvent: LiveData<Event<String>> get() = _snackbarEvent

    private val disposables = CompositeDisposable()

    init {
        disposables.add(
            repository.observeRecentSearches()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list -> _recentSearches.value = list },
                    { error ->
                        Log.e(
                            "WeatherViewModel",
                            "Failed to observe recent searches",
                            error
                        )
                    }
                )
        )
    }

    fun onCitySelected(city: City) {
        lastCity = city
        fetchWeather()
    }

    fun onRetry() {
        fetchWeather(true)
    }

    private fun fetchWeather(forceRefresh: Boolean = false) {
        val city = lastCity ?: return

        _isLoading.value = true

        disposables.add(
            repository.getWeather(city, forceRefresh)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isLoading.value = false }
                .subscribe(
                    { weather -> /* do something */
                        _weather.value = weather
                        _errorMessage.value = null
                    },
                    { error ->
                        val errorCopy = "Unable to load weather from ${city.name}"
                        Log.e("WeatherViewModel", errorCopy, error)
                        _errorMessage.value = errorCopy
                        _snackbarEvent.value = Event(errorCopy)
                    }
                )
        )
    }

    override fun onCleared() {
        disposables.dispose()
    }
}