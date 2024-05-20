package com.example.myfreehealthtracker.foodOpenFacts

import com.example.myfreehealthtracker.OpenFoodFactEntity
import io.ktor.client.*
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.url

class clientFoodOpenFacts() {

    private val client=HttpClient(Android){
        install(Logging){
            level=LogLevel.ALL
        }
        install(JsonFeature){
            serializer=KotlinxSerializer()
        }
    }

    private suspend fun getFoodOpenFacts(barcode: String):List<OpenFoodFactEntity> {
        return client.get {
            url(BASE_URL + GET + barcode)
        }
    }

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.net"
        const val GET = "/api/v2/product/"
    }
}
