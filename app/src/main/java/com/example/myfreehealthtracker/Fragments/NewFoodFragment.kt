@file:Suppress("DEPRECATION")

package com.example.myfreehealthtracker.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.foodOpenFacts.ClientFoodOpenFact
import com.example.myfreehealthtracker.foodOpenFacts.model.ProductResponse
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

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
        try {
            mainApplication = requireActivity().application as MainApplication
        } catch (ex: IllegalStateException) {
            Log.i("NewFoodFragment", "MainApp error")
            Toast.makeText(
                requireContext(),
                "Errore connessione DB prova a riavviare l'applicazione",
                Toast.LENGTH_LONG
            ).show()
        }
        val composeView = view.findViewById<ComposeView>(R.id.fragment_new_food_ComposeView)
        composeView.apply {
            setViewCompositionStrategy(androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyChartParent()
            }
        }

        return view
    }


//    @Preview(showBackground = true)
//    @Composable
//    fun ScrollableListWithCharts() {
//        val itemsList = remember { List(10) { "Item #$it" } }
//
//        LazyColumn(
//            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(itemsList) { item ->
//                Column {
//                    ListItem(item)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    PieChart()
//                }
//            }
//        }
//    }

//    @Composable
//    fun ListItem(item: String) {
//        Surface(
//            shape = MaterialTheme.shapes.medium,
//            tonalElevation = 2.dp,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            PaddingValues(16.dp).apply {
//                Text(
//                    text = item,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(paddingValues = this)
//                )
//            }
//        }
//    }

    @Preview(showBackground = true)
    @Composable
    fun MyChartParent() {

            PieChart(
                pieChartData = PieChartData(
                    listOf(
                        PieChartData.Slice(10F, Color.Red), PieChartData.Slice(
                            20F, Color.Blue
                        ), PieChartData.Slice(30F, Color.Green)
                    )
                ),
                // Optional properties.
                modifier = Modifier.fillMaxSize(),
                animation = simpleChartAnimation(),
                sliceDrawer = SimpleSliceDrawer()
            )

    }

    @Preview(showBackground = true)
    @Composable
    fun ScrollableListWithChartsPreview() {
        //ScrollableListWithCharts()
        MyChartParent()
    }

//    @Preview(showBackground = true)
//    @Composable
//    fun NewFoodScreen() {
//        val prova_lista = remember { List(10) { "Item #$it" } } // Lista di esempio con 10 elementi
//        val alimentList by remember { mutableStateOf(mutableListOf<PastoToCiboWrapper>()) }
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        //.padding(8.dp),
//                ) {
//
//                }
//
//
//                Button(
//                    onClick = {
//                        alimentoWrapper = AlimentoWrapper()
//                        showDialog = true
//                    }) {
//                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
//                }
//
//            }
//
//        }
//        if (showDialog) {
//            AlertDialog(
//                onDismissRequest = { showDialog = false },
//                confirmButton = {
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Button(onClick = {
//                            val integrator =
//                                IntentIntegrator.forSupportFragment(this@NewFoodFragment)
//                            integrator.setPrompt("Scansiona un barcode")
//                            integrator.setCameraId(0)
//                            integrator.setOrientationLocked(true)
//                            integrator.setBeepEnabled(true)
//                            integrator.setDesiredBarcodeFormats(
//                                "EAN_13",
//                                "EAN_8"
//                            )
//                            integrator.setBarcodeImageEnabled(true)
//
//                            integrator.setPrompt("Ciao prompt")
//                            integrator.setOrientationLocked(false)  // Lock the orientation
//                            integrator.captureActivity = PortraitCaptureActivity::class.java
//                            integrator.initiateScan()
//
//                        }) {
//                            Text(text = "Scannerizza")
//                        }
//                        Button(
//                            onClick = {
//                                showDialog = false
//
//                                if (alimentoWrapper.id.isNotEmpty()) {
//                                    val food = alimentoWrapper.convertToFood()
//
//                                    lifecycleScope.launch {
//                                        mainApplication.alimentoDao.insertAlimento(food)
//                                    }
//                                    alimentList.add(
//                                        PastoToCiboWrapper(
//                                            idAlimento = food.id,
//                                            nomeAlimento = food.nome,
//                                            imageUri = food.immagine
//                                        )
//                                    )
//                                    val mainApplication: MainApplication =
//                                        requireActivity().application as MainApplication
//                                    mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.ALIMENTI)
//                                        .child(food.id).setValue(food)
//                                    //add element to list
//                                } else {
//                                    Toast.makeText(
//                                        requireContext(),
//                                        "Nessun elemento inserito",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//
//                            }
//                        ) {
//                            Text(text = "Conferma")
//                        }
//                    }
//                },
//                title = {
//                    Row(
//                        modifier = Modifier,
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(text = "Inserisci alimento")
//
//                        Spacer(modifier = Modifier.width(20.dp))
//
//                        AsyncImage(
//                            model = alimentoWrapper.immagine,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .height(64.dp)
//                                .width(64.dp)
//                        )
//                    }
//
//                },
//                text = {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(8.dp),
//                        modifier = Modifier.verticalScroll(rememberScrollState())
//                    ) {
//                        TextField(
//                            enabled = alimentoWrapper.enabled,
//                            value = alimentoWrapper.id,
//                            onValueChange = {
//                                alimentoWrapper.id = it
//                            },
//                            label = {
//                                Text(text = "Barcode")
//                            }
//                        )
//
//                        TextField(
//                            enabled = alimentoWrapper.enabled,
//                            value = alimentoWrapper.nome,
//                            onValueChange = {
//                                alimentoWrapper.nome = it
//                            },
//                            label = {
//                                Text(text = "nome")
//                            }
//                        )
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//
//                            Text("Unità valori:")
//
//                            RadioButton(
//                                enabled = alimentoWrapper.enabled,
//                                selected = alimentoWrapper.unit == "100g",
//                                onClick = {
//                                    alimentoWrapper.unit = "100g"
//                                }
//                            )
//
//                            Text("100g")
//
//                            RadioButton(
//                                enabled = alimentoWrapper.enabled,
//                                selected = alimentoWrapper.unit == "unit",
//                                onClick = {
//                                    alimentoWrapper.unit = "unit"
//                                }
//                            )
//                            Text("unit")
//
//                        }
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//
//                        ) {
//                            TextField(
//                                modifier = Modifier.weight(1f),
//                                value = alimentoWrapper.carboidrati.toString(),
//                                onValueChange = {
//                                    try {
//                                        alimentoWrapper.carboidrati = it.toFloat()
//                                    } catch (_: Exception) {
//                                    }
//
//                                },
//                                enabled = alimentoWrapper.enabled,
//                                label = {
//                                    Text(text = "Carboidrati")
//                                }
//                            )
//                            TextField(
//                                modifier = Modifier.weight(1f),
//                                enabled = alimentoWrapper.enabled,
//                                value = alimentoWrapper.proteine.toString(),
//                                onValueChange = {
//                                    try {
//                                        alimentoWrapper.proteine = it.toFloat()
//                                    } catch (_: Exception) {
//                                    }
//
//                                },
//                                label = {
//                                    Text(text = "Proteine")
//                                }
//                            )
//
//                        }
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//
//                        ) {
//                            TextField(
//                                modifier = Modifier.weight(1f),
//                                value = alimentoWrapper.calorie.toString(),
//                                onValueChange = {
//                                    try {
//                                        alimentoWrapper.calorie = it.toInt()
//                                    } catch (_: Exception) {
//                                    }
//
//                                },
//                                enabled = alimentoWrapper.enabled,
//                                label = {
//                                    Text(text = "Calorie")
//                                }
//                            )
//                            TextField(
//                                modifier = Modifier.weight(1f),
//                                enabled = alimentoWrapper.enabled,
//                                value = alimentoWrapper.grassi.toString(),
//                                onValueChange = {
//                                    try {
//                                        alimentoWrapper.grassi = it.toFloat()
//                                    } catch (_: Exception) {
//                                    }
//
//                                },
//                                label = {
//                                    Text(text = "Grassi")
//                                }
//                            )
//                            TextField(
//                                modifier = Modifier.weight(1f),
//                                enabled = alimentoWrapper.enabled,
//                                value = alimentoWrapper.sale.toString(),
//                                onValueChange = {
//                                    try {
//                                        alimentoWrapper.sale = it.toFloat()
//                                    } catch (_: Exception) {
//                                    }
//
//                                },
//                                label = {
//                                    Text(text = "Sale")
//                                }
//                            )
//
//                        }
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            TextField(
//                                modifier = Modifier.weight(1f),
//                                value = alimentoWrapper.descrizione,
//                                onValueChange = {
//                                    alimentoWrapper.descrizione = it
//                                },
//                                label = {
//                                    Text(text = "Descrizione")
//                                }
//                            )
//                        }
//
//
//                    }
//
//                }
//            )
//
//        }
//    }


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


//    @Composable
//    fun ItemFood(pastoToCiboWrapper: PastoToCiboWrapper) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(color = Color.LightGray)
//        ) {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//
//                ) {
//                    AsyncImage(
//                        model = pastoToCiboWrapper.imageUri,
//                        contentDescription = null,
//                        modifier = Modifier.weight(1f)
//                    )
//                    pastoToCiboWrapper.nomeAlimento?.let {
//                        Text(
//                            text = it,
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    TextField(
//                        modifier = Modifier.weight(1f),
//                        value = pastoToCiboWrapper.quantita.toString(),
//                        onValueChange = {
//                            try {
//                                pastoToCiboWrapper.quantita = it.toInt()
//                            } catch (_: Exception) {
//                            }
//
//                        },
//                        label = {
//                            Text(text = "Quantita'", modifier = Modifier.weight(1f))
//                        }
//                    )
//                    Text(text = "altro")
//                }
//            }
//        }
//    }
//}

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


    }
}



