package com.example.myfreehealthtracker.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.foodOpenFacts.ClientFoodOpenFact
import com.example.myfreehealthtracker.foodOpenFacts.model.ProductResponse
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewFoodFragment : Fragment() {

    private var showDialog by mutableStateOf(false)
    private var barcode by mutableStateOf("")

    private var alimentoWrapper = AlimentoWrapper()
    private lateinit var mainApplication: MainApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_food, container, false)
        mainApplication = requireActivity().application as MainApplication
        val composeView = view.findViewById<ComposeView>(R.id.fragment_new_food_ComposeView)
        composeView.apply {
            setViewCompositionStrategy(androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NewFoodScreen()
            }
        }

        return view
    }

    @Preview(showBackground = true)
    @Composable
    fun NewFoodScreen() {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(

                ) {

                }
                Button(
                    onClick = {
                        alimentoWrapper = AlimentoWrapper()
                        showDialog = true
                    }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }

            }

        }
        if (showDialog) {

            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            val integrator =
                                IntentIntegrator.forSupportFragment(this@NewFoodFragment)
                            integrator.setPrompt("Scansiona un barcode") // Testo mostrato sopra l'area di scansione
                            integrator.setCameraId(0) // Usa la fotocamera posteriore
                            integrator.setOrientationLocked(true) // Imposta il blocco dell'orientamento
                            integrator.setBeepEnabled(false) // Disabilita il segnale acustico alla scansione

                            integrator.initiateScan()

                        }) {
                            Text(text = "Scannerizza")
                        }
                        Button(
                            onClick = {
                                showDialog = false


                                val food = alimentoWrapper.convertToFood()

                                lifecycleScope.launch {
                                    mainApplication.alimentoDao.insertAlimento(food)
                                }
                            }
                        ) {
                            Text(text = "Conferma")
                        }
                    }
                },
                title = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Inserisci alimento")

                        Spacer(modifier = Modifier.width(20.dp))

                        AsyncImage(
                            model = alimentoWrapper.immagine,
                            contentDescription = null,
                            modifier = Modifier
                                .height(64.dp)
                                .width(64.dp)
                        )
                    }

                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextField(
                            enabled = alimentoWrapper.enabled,
                            value = alimentoWrapper.id,
                            onValueChange = {
                                alimentoWrapper.id = it
                            },
                            label = {
                                Text(text = "Barcode")
                            }
                        )

                        TextField(
                            enabled = alimentoWrapper.enabled,
                            value = alimentoWrapper.nome,
                            onValueChange = {
                                alimentoWrapper.nome = it
                            },
                            label = {
                                Text(text = "nome")
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text("Unità valori:")

                            RadioButton(
                                enabled = alimentoWrapper.enabled,
                                selected = alimentoWrapper.unit == "100g",
                                onClick = {
                                    alimentoWrapper.unit = "100g"
                                }
                            )

                            Text("100g")

                            RadioButton(
                                enabled = alimentoWrapper.enabled,
                                selected = alimentoWrapper.unit == "unit",
                                onClick = {
                                    alimentoWrapper.unit = "unit"
                                }
                            )
                            Text("unit")

                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = alimentoWrapper.carboidrati.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.carboidrati = it.toFloat()
                                    } catch (e: Exception) {
                                    }

                                },
                                enabled = alimentoWrapper.enabled,
                                label = {
                                    Text(text = "Carboidrati")
                                }
                            )
                            TextField(
                                modifier = Modifier.weight(1f),
                                enabled = alimentoWrapper.enabled,
                                value = alimentoWrapper.proteine.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.proteine = it.toFloat()
                                    } catch (e: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Proteine")
                                }
                            )

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = alimentoWrapper.calorie.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.calorie = it.toInt()
                                    } catch (e: Exception) {
                                    }

                                },
                                enabled = alimentoWrapper.enabled,
                                label = {
                                    Text(text = "Calorie")
                                }
                            )
                            TextField(
                                modifier = Modifier.weight(1f),
                                enabled = alimentoWrapper.enabled,
                                value = alimentoWrapper.grassi.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.grassi = it.toFloat()
                                    } catch (e: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Grassi")
                                }
                            )
                            TextField(
                                modifier = Modifier.weight(1f),
                                enabled = alimentoWrapper.enabled,
                                value = alimentoWrapper.sale.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.sale = it.toFloat()
                                    } catch (e: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Sale")
                                }
                            )

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = alimentoWrapper.descrizione,
                                onValueChange = {
                                    alimentoWrapper.descrizione = it
                                },
                                label = {
                                    Text(text = "Descrizione")
                                }
                            )
                        }


                    }

                }
            )

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
                barcode = result.contents
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
                        Log.i("Stato product", food.nome.toString())
                        if (food.nome != null) {
                            alimentoWrapper.enabled = false
                            alimentoWrapper.convertToWrapper(food)
                        } else {
                            Log.i("Test", food.toString())
                            Toast.makeText(
                                requireContext(),
                                "Prodotto non trovato",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private suspend fun callClient(s: String): ProductResponse {
        return ClientFoodOpenFact().getFoodOpenFacts(s)
    }

    private class AlimentoWrapper(

    ) {

        var id: String by mutableStateOf("")

        var nome: String by mutableStateOf("")

        var immagine: String by mutableStateOf("")

        var unit: String by mutableStateOf("")

        var carboidrati: Float by mutableStateOf(0f)

        var proteine: Float by mutableStateOf(0f)

        var grassi: Float by mutableStateOf(0f)

        var sale: Float by mutableStateOf(0f)

        var calorie: Int by mutableStateOf(0)

        var descrizione: String by mutableStateOf("")

        var enabled by mutableStateOf(true)

        fun convertToWrapper(alimento: Alimento) {
            id = alimento.id
            nome = alimento.nome ?: ""
            immagine = alimento.immagine ?: ""
            unit = alimento.unit ?: ""
            carboidrati = alimento.carboidrati ?: 0f
            proteine = alimento.proteine ?: 0f
            grassi = alimento.grassi ?: 0f
            sale = alimento.sale ?: 0f
            calorie = alimento.calorie ?: 0
            descrizione = alimento.descrizione ?: ""
        }
        fun convertToFood(): Alimento {
            return Alimento(
                id,
                nome,
                immagine,
                unit,
                carboidrati,
                proteine,
                grassi,
                sale,
                calorie,
                descrizione
            )
        }


    }


}

