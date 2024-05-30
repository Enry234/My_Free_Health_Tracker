package com.example.myfreehealthtracker.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.PortraitCaptureActivity
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.foodOpenFacts.ClientFoodOpenFact
import com.example.myfreehealthtracker.foodOpenFacts.model.ProductResponse
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {
    private var alimentoWrapper = AlimentoWrapper()
    val alimentList by mutableStateOf(mutableListOf<PastoToCiboWrapper>())

    private lateinit var mainApplication: MainApplication

    private var canConfirmMeal by mutableStateOf(false)
    private var showAddFoodDialog by mutableStateOf(false)
    private var showNewFoodDialog by mutableStateOf(false)
    private var barcode by mutableStateOf("")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            mainApplication = requireActivity().application as MainApplication
            alimentoWrapper.loadFromString(savedInstanceState?.getString("alimentoWrapper"))
        } catch (ex: Exception) {
            Log.i("NewFoodFragment", "MainApp error")
            Toast.makeText(
                requireContext(),
                "Errore connessione DB prova a riavviare l'applicazione",
                Toast.LENGTH_LONG
            ).show()
        }

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AddMealScreen()
            }
        }
    }

    @Composable
    private fun AddMealScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nuovo Pasto",
                    fontSize = 24.sp
                )
                Button(
                    onClick = {
                        showAddFoodDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Alimento")
                }
            }
            LazyColumn(

            ) {
                items(alimentList.size) {
                    ItemFood(pastoToCiboWrapper = alimentList[it])
                }
            }
            Button(
                enabled = canConfirmMeal,
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Conferma Inserimento")
            }
        }

        if (showAddFoodDialog) {
            AddFoodDialog()
        } else if (showNewFoodDialog) {
            NewFoodDialog()
        }
    }

    @Composable
    fun ItemFood(pastoToCiboWrapper: PastoToCiboWrapper) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    AsyncImage(
                        model = pastoToCiboWrapper.imageUri,
                        contentDescription = null,
                        modifier = Modifier.weight(1f)
                    )
                    pastoToCiboWrapper.nomeAlimento?.let {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = pastoToCiboWrapper.quantita.toString(),
                        onValueChange = {
                            try {
                                pastoToCiboWrapper.quantita = it.toInt()
                            } catch (ex: Exception) {
                            }

                        },
                        label = {
                            Text(text = "Quantita'", modifier = Modifier.weight(1f))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )

                    )
                    Text(text = "altro")
                }
            }
        }
    }

    @Composable
    private fun AddFoodDialog() {
        AlertDialog(
            onDismissRequest = {
                showAddFoodDialog = false
            },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showAddFoodDialog = false
                    }
                ) {
                    Text(text = "Annulla")
                }
            },
            title = {
                Text(text = "Aggiungi Alimento")
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {

                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Esistente")
                    }
                    Button(
                        onClick = {
                            showAddFoodDialog = false
                            alimentoWrapper = AlimentoWrapper()
                            showNewFoodDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Nuovo")
                    }
                }
            }
        )
    }

    @Composable
    private fun NewFoodDialog() {

        AlertDialog(
            onDismissRequest = { showNewFoodDialog = false },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        val integrator =
                            IntentIntegrator.forSupportFragment(this@NewMealFragment)
                        integrator.setPrompt("Scansiona un barcode") // Testo mostrato sopra l'area di scansione
                        integrator.setCameraId(0) // Usa la fotocamera posteriore
                        integrator.setOrientationLocked(true) // Imposta il blocco dell'orientamento
                        integrator.setBeepEnabled(true) // Disabilita il segnale acustico alla scansione
                        integrator.setDesiredBarcodeFormats(
                            "EAN_13",
                            "EAN_8"
                        ) // Mostra l'immagine del codice a barre
                        integrator.setBarcodeImageEnabled(true) // Mostra l'immagine del codice a barre

                        integrator.setPrompt("Ciao prompt")
                        integrator.setOrientationLocked(false)  // Lock the orientation
                        integrator.captureActivity = PortraitCaptureActivity::class.java
                        integrator.initiateScan()

                    }) {
                        Text(text = "Scannerizza")
                    }
                    Button(
                        onClick = {
                            showNewFoodDialog = false

                            if (alimentoWrapper.id.isNotEmpty()) {
                                val food = alimentoWrapper.convertToFood()

                                lifecycleScope.launch {
                                    mainApplication.alimentoDao.insertAlimento(food)
                                }
                                alimentList.add(
                                    PastoToCiboWrapper(
                                        idAlimento = food.id,
                                        nomeAlimento = food.nome,
                                        imageUri = food.immagine
                                    )
                                )
                                val mainApplication: MainApplication =
                                    requireActivity().application as MainApplication
                                mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.ALIMENTI)
                                    .child(food.id).setValue(food)
                                //add element to list
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Nessun elemento inserito",
                                    Toast.LENGTH_LONG
                                ).show()
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
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    TextField(
                        enabled = alimentoWrapper.enabled,
                        value = alimentoWrapper.id,
                        onValueChange = {
                            alimentoWrapper.id = it
                        },
                        label = {
                            Text(text = "Barcode")
                        },
                        placeholder = {
                            Text(text = "Inserisci il Barcode")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
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
                                } catch (ex: Exception) {
                                }

                            },
                            enabled = alimentoWrapper.enabled,
                            label = {
                                Text(text = "Carboidrati")
                            },
                            placeholder = {
                                Text(text = "Carboidrati")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )
                        TextField(
                            modifier = Modifier.weight(1f),
                            enabled = alimentoWrapper.enabled,
                            value = alimentoWrapper.proteine.toString(),
                            onValueChange = {
                                try {
                                    alimentoWrapper.proteine = it.toFloat()
                                } catch (ex: Exception) {
                                }

                            },
                            label = {
                                Text(text = "Proteine")
                            },
                            placeholder = {
                                Text(text = "Proteine")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
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
                                } catch (ex: Exception) {
                                }

                            },
                            enabled = alimentoWrapper.enabled,
                            label = {
                                Text(text = "Calorie")
                            },
                            placeholder = {
                                Text(text = "Calorie")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )
                        TextField(
                            modifier = Modifier.weight(1f),
                            enabled = alimentoWrapper.enabled,
                            value = alimentoWrapper.grassi.toString(),
                            onValueChange = {
                                try {
                                    alimentoWrapper.grassi = it.toFloat()
                                } catch (ex: Exception) {
                                }

                            },
                            label = {
                                Text(text = "Grassi")
                            },
                            placeholder = {
                                Text(text = "Grassi")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )
                        TextField(
                            modifier = Modifier.weight(1f),
                            enabled = alimentoWrapper.enabled,
                            value = alimentoWrapper.sale.toString(),
                            onValueChange = {
                                try {
                                    alimentoWrapper.sale = it.toFloat()
                                } catch (ex: Exception) {
                                }

                            },
                            label = {
                                Text(text = "Sale")
                            },
                            placeholder = {
                                Text(text = "Sale")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
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

    @Deprecated("Deprecated in Java")
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
                val toast = Toast.makeText(
                    requireContext(),
                    "Codice a barre: $barcode, Caricamento dati in corso",
                    Toast.LENGTH_LONG
                )
                toast.show()
                val scope = CoroutineScope(Dispatchers.Main)

                scope.launch {
                    var product: ProductResponse? = null
                    // Call the suspend function
                    try {
                        product = callClient(result.contents)
                    } catch (ex: Exception) {

                    }
                    toast.cancel()

                    if (product == null || product.status == 0) {
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

    data class PastoToCiboWrapper(
        var idAlimento: String = "",
        var idDate: Date = Date(),
        var idUser: String = "",
        var nomeAlimento: String? = "",
        var imageUri: String? = "",
        var quantita: Int = 1
    )

    private class AlimentoWrapper {

        var id: String by mutableStateOf("")

        var nome: String by mutableStateOf("")

        var immagine: String by mutableStateOf("")

        var unit: String by mutableStateOf("")

        var carboidrati: Float by mutableFloatStateOf(0f)

        var proteine: Float by mutableFloatStateOf(0f)

        var grassi: Float by mutableFloatStateOf(0f)

        var sale: Float by mutableFloatStateOf(0f)

        var calorie: Int by mutableIntStateOf(0)

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

        fun loadFromString(s: String?) {
            if (s != null) {

                val split = s.split("|")
                id = split[0]
                nome = split[1]
                immagine = split[2]
                unit = split[3]
                carboidrati = split[4].toFloat()
                proteine = split[5].toFloat()
                grassi = split[6].toFloat()
                sale = split[7].toFloat()
                calorie = split[8].toInt()
                descrizione = split[9]

            }
        }
    }
}