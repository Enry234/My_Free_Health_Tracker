package com.example.myfreehealthtracker.localdatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfreehealthtracker.foodopenfacts.model.ProductResponse

@Entity
data class Alimento(
    @PrimaryKey
    var id: String = "",

    @ColumnInfo(name = "nome")
    var nome: String? = "",

    @ColumnInfo(name = "immagine")
    var immagine: String? = "",

    @ColumnInfo(name = "unit")
    var unit: String? = "",

    @ColumnInfo(name = "carboidrati")
    var carboidrati: Float? = 0f,

    @ColumnInfo(name = "proteine")
    var proteine: Float? = 0f,

    @ColumnInfo(name = "fibre")
    var fibre: Float? = null,

    @ColumnInfo(name = "grassi")
    var grassi: Float? = 0f,

    @ColumnInfo(name = "sale")
    var sale: Float? = 0f,

    @ColumnInfo(name = "calorie")
    var calorie: Int? = 0,

    @ColumnInfo(name = "descrizione")
    var descrizione: String? = ""


    ) {

    companion object {
        fun convertToAlimento(food: ProductResponse): Alimento {

            val id: String = food.code.toString()
            var nome = food.product?.productName
            var immagine = food.product?.imageUrl
            val unit = food.product?.nutritionDataPreparedPer
            val carboidrati = food.product?.nutriments?.carbohydrates
            val proteine = food.product?.nutriments?.proteins
            val grassi = food.product?.nutriments?.fat
            val fibre = food.product?.nutriments?.fiber
            val sale = food.product?.nutriments?.salt
            val calorie = food.product?.nutriments?.energyKcal
            val descrizione = food.product?.dataSources

            if (nome.isNullOrEmpty() || nome.isNullOrBlank()) {
                val foodList = listOf(
                    food.product?.productName_bg,
                    food.product?.productName_en,
                    food.product?.productName_fr,
                    food.product?.productName_es,
                    food.product?.productName_pt,
                    food.product?.productName_uk
                )

                foodList.forEach {
                    if (!it.isNullOrEmpty() && !it.isNullOrBlank()) {
                        nome = it
                    }
                }
            }

            if (immagine.isNullOrEmpty() || immagine.isNullOrBlank()) {
                val imageList = listOf(
                    food.product?.imageFrontSmallUrl,
                    food.product?.imageFrontThumbUrl,
                    food.product?.imageFrontUrl,
                )

                imageList.forEach {
                    if (!it.isNullOrEmpty() && !it.isNullOrBlank()) {
                        immagine = it
                    }
                }
            }



            return Alimento(
                id,
                nome,
                immagine,
                unit,
                carboidrati,
                proteine,
                fibre,
                grassi,
                sale,
                calorie,
                descrizione
            )
        }
    }


}
