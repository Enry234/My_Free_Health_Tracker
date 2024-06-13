package com.example.myfreehealthtracker.foodOpenFacts.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// Was: <String, Any>
@Serializable
data class Images(
    var other: MutableMap<String, JsonElement> = LinkedHashMap()
) {
    // Was: (key: String, value: Any)
    fun setDetail(key: String, value: JsonElement) {
        other[key] = value
    }
}
