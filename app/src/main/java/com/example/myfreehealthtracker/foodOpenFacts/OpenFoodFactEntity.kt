package com.example.myfreehealthtracker.foodOpenFacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OpenFoodFactEntity(
    val code: String,
    val abbreviated_product_name: String,
    val product_name: String,
    val no_nutrition_data: String,
    val nutrition_data_per: NutritionLabelServingSize,
    val nutriments: Nutriments
)

@Serializable
data class Nutriments(
    val alcohol: Double,
    val carbohydrates: Double,
    @SerialName("energy-kcal") val energy_kcal: Double,
    val fat: Double,
    val proteins: Double,
    val salt: Double
)


enum class NutritionLabelServingSize(val label: String) {
    SERVING("serving"),
    PER_100G("100g")
}