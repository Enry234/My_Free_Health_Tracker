@file:Suppress("DEPRECATION")

package com.example.myfreehealthtracker.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalDBViewModel
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalViewModelFactory
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.PortraitCaptureActivity
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

class NewFoodFragment : Fragment() {

    private var barcode by mutableStateOf("")

    private var alimentoWrapper = AlimentoWrapper()
    private lateinit var mainApplication: MainApplication

    private val alimentoViewModel: InternalDBViewModel by viewModels {
        InternalViewModelFactory(
            mainApplication.userRepository,
            mainApplication.alimentoRepository,
            mainApplication.pastoRepository,
            mainApplication.pastoToCiboRepository,
            mainApplication.sportRepository,
            mainApplication.attivitaRepository
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("alimentoWrapper", alimentoWrapper.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_new_food, container, false)
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

        var showDialog by rememberSaveable {
            mutableStateOf(false)
        }
        val alimentList by alimentoViewModel.allAlimento.observeAsState(initial = emptyList())
        Surface(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(alimentList.size) {
                        ItemFood2(alimento = alimentList[it])
                    }
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
            var error by rememberSaveable {
                mutableStateOf(false)
            }
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


                                if (alimentoWrapper.id.isNotEmpty()) {
                                    showDialog = false
                                    val food = alimentoWrapper.convertToFood()

                                    lifecycleScope.launch {
                                        mainApplication.alimentoRepository.insertAlimento(food)
                                    }

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
                                    error = true
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
                            isError = error && alimentoWrapper.enabled && alimentoWrapper.id.isEmpty(),
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
                            isError = error && alimentoWrapper.enabled && alimentoWrapper.nome.isEmpty(),
                            value = alimentoWrapper.nome,
                            onValueChange = {
                                alimentoWrapper.nome = it
                            },
                            label = {
                                Text(text = "Nome")
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text("Unità valori:")

                            RadioButton(

                                selected = alimentoWrapper.unit == "100g",
                                onClick = {
                                    alimentoWrapper.unit = "100g"
                                }
                            )

                            Text("100g")

                            RadioButton(
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

                                modifier = Modifier.weight(1.2f),
                                value = alimentoWrapper.carboidrati.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.carboidrati = it.toFloat()
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Carboidrati")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )
                            TextField(

                                modifier = Modifier.weight(1f),
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
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )

                            TextField(
                                modifier = Modifier.weight(0.8f),
                                value = alimentoWrapper.fibre.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.fibre = it.toFloat()
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Fibre")
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
                                modifier = Modifier.weight(1.2f),
                                value = alimentoWrapper.calorie.toString(),
                                onValueChange = {
                                    try {
                                        alimentoWrapper.calorie = it.toInt()
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Calorie")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )
                            TextField(
                                modifier = Modifier.weight(1f),
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
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )
                            TextField(
                                modifier = Modifier.weight(0.8f),
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
                        Toast.makeText(
                            requireContext(),
                            "PRODOTTO NON TROVATO INSERIRE MANUALMENTE",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.i("load error", "errore caricamento")
                        Log.i("load error", ex.toString())
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


    @Composable
    fun ItemFood2(alimento: Alimento) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .border(
//                    width = 2.dp,
//                    color = Color.Black,
//                    shape= RoundedCornerShape(8.dp)
//                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    //NOME E IMMAGINE ALIMENTO
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    alimento.nome?.let {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = it,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Row() {
                        AsyncImage(
                            model = alimento.immagine,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .height(56.dp)
                                .width(56.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Row(
                    //PIECHART CON BOX, E UN ALTRO BOX PER METTERE I VALORI NUTRIZIONALI
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(75.dp)
                            .width(75.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(
                            pieChartData = PieChartData(
                                listOf(
                                    PieChartData.Slice(
                                        alimento.carboidrati ?: 0f,
                                        Color(0xFFE1E289)
                                    ),
                                    PieChartData.Slice(alimento.proteine ?: 0f, Color(0xFFDB504A)),
                                    PieChartData.Slice(alimento.grassi ?: 0f, Color(0xFF59C3C3)),
                                    PieChartData.Slice(alimento.fibre ?: 0f, Color(0xFF04724D)),
                                )
                            ),
                            modifier = Modifier.fillMaxSize(),
                            animation = simpleChartAnimation(),
                            sliceDrawer = SimpleSliceDrawer(sliceThickness = 15F)
                        )
                        Column(

                        ) {

                            Text(
                                text = alimento.calorie.toString()
                            )

                            Text(
                                text = "kcal",
                            )

                        }

                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(.8f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFF59C3C3),
                                            label = "Grassi",
                                            value = alimento.grassi?.toInt() ?: 0
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFF04724D),
                                            label = "Fibre",
                                            value = alimento.fibre?.toInt() ?: 0
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFFE1E289),
                                            label = "Carboidrati",
                                            value = alimento.carboidrati?.toInt() ?: 0
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFFDB504A),
                                            label = "Proteine",
                                            value = alimento.proteine?.toInt() ?: 0
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    @Composable
    fun BulletSpan(color: Color, label: String, value: Int) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(10.dp)
                .height(10.dp)
        ) {
            //internal circle with icon
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "contentDescription",
                modifier = Modifier
                    .width(24.dp)
                    .background(color, CircleShape)
                    .padding(2.dp),
                tint = color
            )
        }
        Text(
            text = "$label: ${value}g"
        )
    }


    private suspend fun callClient(s: String): ProductResponse {
        return ClientFoodOpenFact().getFoodOpenFacts(s)
    }

    private class AlimentoWrapper {

        var id: String by mutableStateOf("")

        var nome: String by mutableStateOf("")

        var immagine: String by mutableStateOf("")

        var unit: String by mutableStateOf("")

        var carboidrati: Float by mutableFloatStateOf(0f)

        var proteine: Float by mutableFloatStateOf(0f)

        var fibre: Float by mutableFloatStateOf(0f)

        var grassi: Float by mutableFloatStateOf(0f)

        var sale: Float by mutableFloatStateOf(0f)

        var calorie: Int by mutableIntStateOf(0)

        var descrizione: String by mutableStateOf("")

        var enabled by mutableStateOf(true)

        override fun toString(): String {
            return "$id|$nome|$immagine|$unit|$carboidrati|$proteine|$fibre|$grassi|$sale|$calorie|$descrizione"
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
                fibre = split[6].toFloat()
                grassi = split[7].toFloat()
                sale = split[8].toFloat()
                calorie = split[9].toInt()
                descrizione = split[10]

            }
        }

        fun convertToWrapper(alimento: Alimento) {
            id = alimento.id
            nome = alimento.nome ?: ""
            immagine = alimento.immagine ?: ""
            unit = alimento.unit ?: ""
            carboidrati = alimento.carboidrati ?: 0f
            proteine = alimento.proteine ?: 0f
            fibre = alimento.fibre ?: 0f
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
                fibre,
                grassi,
                sale,
                calorie,
                descrizione
            )
        }


    }
}



