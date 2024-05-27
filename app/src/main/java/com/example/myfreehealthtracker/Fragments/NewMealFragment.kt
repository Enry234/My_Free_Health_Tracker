package com.example.myfreehealthtracker.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.foodOpenFacts.ClientFoodOpenFact
import com.example.myfreehealthtracker.foodOpenFacts.model.ProductResponse
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {

    private var foodList = mutableStateListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodList.add("Testpizza")
        val confirmMealBtn: Button = view.findViewById(R.id.fragment_new_meal_confirm)

        confirmMealBtn.setOnClickListener {
            //TODO
            view.findNavController().navigate(
                R.id.action_confirm_new_meal,
                bundleOf("content" to "placeholder")
            )
        }

        val dialogAddFood = Dialog(view.context)
        dialogAddFood.setContentView(R.layout.fragment_new_meal_add_food_dialog)

        val addNewFoodBtn: Button = view.findViewById(R.id.fragment_new_meal_add_food)
        addNewFoodBtn.setOnClickListener {
            dialogAddFood.show()
        }

        dialogAddFood.findViewById<Button>(R.id.dialog_food_cancel_button).setOnClickListener {
            dialogAddFood.dismiss()
        }

        dialogAddFood.findViewById<Button>(R.id.dialog_food_existing_button).setOnClickListener {
            val dialogAddExistingFood = Dialog(view.context)
            dialogAddExistingFood.setContentView(R.layout.dialog_new_food_spinner)
            val spinner = dialogAddExistingFood.findViewById<Spinner>(R.id.dialog_existing_spinner)

            val existingFoodItems = listOf(
                "pizzooooooooooooooooooooooooo",
                "trette",
                "calle",
                "carletto",
                "pizzo",
                "trette",
                "calle",
                "carletto",
                "pizzo",
                "trette",
                "calle",
                "carletto",
                "pizzo",
                "trette",
                "calle",
                "carletto"
            )
            val foodAdapter =
                ArrayAdapter(view.context, android.R.layout.simple_spinner_item, existingFoodItems)
            foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = foodAdapter

            dialogAddExistingFood.setOnCancelListener {
                dialogAddFood.show()
            }
            dialogAddFood.dismiss()
            dialogAddExistingFood.show()

            dialogAddExistingFood.findViewById<Button>(R.id.dialog_existing_spinner_cancel)
                .setOnClickListener {
                    dialogAddExistingFood.dismiss()
                    dialogAddFood.show()
                }

            dialogAddExistingFood.findViewById<Button>(R.id.dialog_existing_spinner_confirm)
                .setOnClickListener {
                    foodList.add(spinner.selectedItem.toString())
                    dialogAddExistingFood.dismiss()
                }
        }

        dialogAddFood.findViewById<Button>(R.id.dialog_food_new_button).setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setPrompt("Scansiona un barcode") // Testo mostrato sopra l'area di scansione
            integrator.setCameraId(0) // Usa la fotocamera posteriore
            integrator.setOrientationLocked(true) // Imposta il blocco dell'orientamento
            integrator.setBeepEnabled(false) // Disabilita il segnale acustico alla scansione

            integrator.initiateScan()
            dialogAddFood.dismiss()
        }

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight()
                ) {
                    items(foodList) { food ->
                        Item(food)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //val outputBarcode = view?.findViewById<TextView>(R.id.output_barcode)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                //outputBarcode?.text = "Scansione fallita"
            } else {
                // Il codice a barre è stato trovato
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    // Call the suspend function
                    val product = callClient(result.contents)
                    if (product.status == 0) {
                        Toast.makeText(
                            requireContext(),
                            "Prodotto non trovato",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val food = Alimento.convertToAlimento(product)
                        Log.i("Test", food.toString())
                        Toast.makeText(
                            requireContext(),
                            food.nome.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private suspend fun callClient(s: String): ProductResponse {
        return ClientFoodOpenFact().getFoodOpenFacts(s)
    }
}

@Composable
fun Item(string: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(color = Color.LightGray)
            .fillMaxHeight(0.2f)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = string)
            Text(text = "Quantita")
            Text(text = "altro testo")

        }

    }

}

//@Preview(showBackground = true)
//@Composable
//fun AddExistingFoodDialog(val currentBarcode) {
//    AlertDialog(
//        modifier = Modifier.fillMaxWidth(),
//        onDismissRequest = {
//
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//
//                }
//            ) {
//                Text(text = "Conferma")
//            }
//        },
//        title = {
//            Text(text = "Aggiunti esistente")
//        },
//        text = {
//            Box(
//
//            ) {
//                Button(onClick = {
//
//                }) {
//                    Text(text = )
//                }
//            }
//        }
//    )
//}