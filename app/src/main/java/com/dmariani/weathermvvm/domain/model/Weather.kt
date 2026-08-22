package com.dmariani.weathermvvm.domain.model

data class Weather (
    val city: String,
    val temperature: Double,
    val condition: String,
    val isDay: Boolean
)