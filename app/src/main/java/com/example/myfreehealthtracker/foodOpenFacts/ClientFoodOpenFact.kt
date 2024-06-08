package com.example.myfreehealthtracker.foodOpenFacts

import com.example.myfreehealthtracker.foodOpenFacts.model.ProductResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.url

class ClientFoodOpenFact() {

    private val client = HttpClient(Android) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                json = kotlinx.serialization.json.Json {
                    encodeDefaults = true
                    isLenient = true
                    allowSpecialFloatingPointValues = true
                    allowStructuredMapKeys = true
                    prettyPrint = false
                    useArrayPolymorphism = false
                    ignoreUnknownKeys = true

                }
            )
        }
    }

    suspend fun getFoodOpenFacts(barcode: String): ProductResponse {
        return client.get {
            url(BASE_URL + GET + barcode)
        }
    }

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org"
        const val GET = "/api/v0/product/"
    }


}
