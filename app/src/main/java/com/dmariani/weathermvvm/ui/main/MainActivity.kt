package com.dmariani.weathermvvm.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.dmariani.weathermvvm.databinding.ActivityMainBinding
import com.dmariani.weathermvvm.ui.common.ViewModelFactory
import javax.inject.Inject
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmariani.weathermvvm.R
import com.dmariani.weathermvvm.WeatherApp
import com.dmariani.weathermvvm.domain.model.Cities
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityMainBinding

    private val viewModel: WeatherViewModel by viewModels { viewModelFactory }

    private lateinit var recentSearchAdapter: RecentSearchAdapter

    private var lastSelectedCityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as WeatherApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // true = dark icons on light background
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val padding = (16 * resources.displayMetrics.density).toInt()
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left + padding,
                systemBars.top + padding,
                systemBars.right + padding,
                systemBars.bottom + padding)
            insets
        }

        // Initialize last city
        lastSelectedCityName = viewModel.currentCity?.name

        // Initialize Spinner
        val cityNames = Cities.ALL.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val city = Cities.ALL[position]
                if (city.name == lastSelectedCityName) return
                lastSelectedCityName = city.name
                viewModel.onCitySelected(city)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }

        // Initialize RecyclerView
        recentSearchAdapter = RecentSearchAdapter()
        binding.listRecentSearch.adapter = recentSearchAdapter
        binding.listRecentSearch.layoutManager = LinearLayoutManager(this)

        // Initialize Retry Button
        binding.buttonRetry.setOnClickListener {
            viewModel.onRetry()
        }

        // Observe weather events
        viewModel.weather.observe(this) { weather ->
            // Set Weather data
            binding.textLocation.text = getString(R.string.city_weather, weather.city)
            binding.textTemperature.text = getString(R.string.city_temperature, weather.temperature)
            val conditionTextResource = if (weather.isDay) R.string.city_condition_day else R.string.city_condition_night
            binding.textCondition.text = getString(conditionTextResource, weather.condition)

            // Update State
            binding.groupWeather.visibility = View.VISIBLE
            binding.groupError.visibility = View.GONE
        }

        // Observe loading events
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error events
        viewModel.errorMessage.observe(this) { error ->
            binding.groupError.visibility = if (error != null) View.VISIBLE else View.GONE
            binding.textError.text = error ?: ""

            if (error != null) {
                binding.groupWeather.visibility = View.GONE
            }
        }

        // Observe recent search events
        viewModel.recentSearches.observe(this) { recentCities ->
            recentSearchAdapter.submitList(recentCities)
        }

        // Observe snackbar events
        viewModel.snackbarEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}