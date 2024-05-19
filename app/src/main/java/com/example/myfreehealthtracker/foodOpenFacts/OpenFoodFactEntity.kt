package com.example.myfreehealthtracker.foodOpenFacts

import kotlinx.serialization.Serializable
import org.junit.Test

@Serializable
data class OpenFoodFactEntity(
    val code: String
)