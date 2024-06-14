@file:Suppress("DEPRECATION")

package com.example.myfreehealthtracker.viewmodel.app.fragments

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.ApplicationTheme
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.foodopenfacts.ClientFoodOpenFact
import com.example.myfreehealthtracker.foodopenfacts.model.ProductResponse
import com.example.myfreehealthtracker.localdatabase.Entities.Alimento
import com.example.myfreehealthtracker.localdatabase.ViewModels.InternalDBViewModel
import com.example.myfreehealthtracker.localdatabase.ViewModels.InternalViewModelFactory
import com.example.myfreehealthtracker.utils.PortraitCaptureActivity
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewFoodFragment : Fragment() {

    private var barcode by mutableStateOf("")

    private var alimentoWrapper = AlimentoWrapper()
    private lateinit var mainApplication: MainApplication

    private val dbViewModel: InternalDBViewModel by viewModels {
        InternalViewModelFactory(
            mainApplication.alimentoRepository,
            mainApplication.pastoRepository,
            mainApplication.pastoToCiboRepository,
            mainApplication.sportRepository,
            mainApplication.attivitaRepository
        )
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("alimentoWrapper", alimentoWrapper.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        val view = inflater.inflate(R.layout.fragment_new_food, container, false)
        try {
            mainApplication = requireActivity().application as MainApplication
            alimentoWrapper.loadFromString(savedInstanceState?.getString("alimentoWrapper"))
        } catch (ex: Exception) {
            Log.i("NewFoodFragment", "MainApp error")
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.dbConnectionError),
                Toast.LENGTH_LONG
            ).show()
        }
        mainApplication.userData!!.userData.observe(viewLifecycleOwner) {
            if (mainApplication.userData!!.userData.value != null) {
                val composeView = view.findViewById<ComposeView>(R.id.fragment_new_food_ComposeView)
                composeView.apply {
                    setViewCompositionStrategy(androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        ApplicationTheme {
                            NewFoodScreen()
                        }

                    }
                }
            }
        }

        return view
    }


    /**
     * Utilizza una LazyColumn per visualizzare
     * tutti gli alimenti aggiunti dall'utente nella applicazione
     */
    @Preview(showBackground = true)
    @Composable
    fun NewFoodScreen() {

        var showDialog by rememberSaveable {
            mutableStateOf(false)
        }
        val alimentList by dbViewModel.allAlimento.observeAsState(initial = emptyList())
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        alimentoWrapper = AlimentoWrapper()
                        showDialog = true
                    },
                    modifier = Modifier
                        .size(56.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(alimentList.size) {
                    ItemFood(alimento = alimentList[it])
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
                            integrator.setPrompt(requireContext().getString(R.string.scanABarcode)) // Testo mostrato sopra l'area di scansione
                            integrator.setCameraId(0) // Usa la fotocamera posteriore
                            integrator.setOrientationLocked(true) // Imposta il blocco dell'orientamento
                            integrator.setBeepEnabled(true) // Disabilita il segnale acustico alla scansione
                            integrator.setDesiredBarcodeFormats(
                                "EAN_13",
                                "EAN_8"
                            ) // Mostra l'immagine del codice a barre
                            integrator.setBarcodeImageEnabled(true) // Mostra l'immagine del codice a barre
                            integrator.setOrientationLocked(false)  // Lock the orientation
                            integrator.captureActivity = PortraitCaptureActivity::class.java
                            integrator.initiateScan()

                        }) {
                            Text(text = stringResource(id = R.string.scanner))
                        }
                        Button(

                            onClick = {


                                if (alimentoWrapper.id.isNotEmpty()) {
                                    showDialog = false
                                    val food = alimentoWrapper.convertToFood()


                                    dbViewModel.insertAlimento(food)


                                    mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.ALIMENTI)
                                        .child(food.id).setValue(food)
                                    val bundle = Bundle().apply {
                                        putString(
                                            "id",
                                            mainApplication.userData!!.userData.value!!.id
                                        )
                                    }
                                    firebaseAnalytics.logEvent("hasInsertedFood", bundle)

                                    //add element to list
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        requireContext().getString(R.string.noElementInserted),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    error = true
                                }

                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                },
                title = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.insertAlimento))

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
                                Text(text = stringResource(id = R.string.barcode))
                            },
                            placeholder = {
                                Text(text = stringResource(id = R.string.insertBarcode))
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
                                Text(text = stringResource(id = R.string.name))
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(stringResource(id = R.string.unitValue))

                            RadioButton(

                                selected = alimentoWrapper.unit == "100g",
                                onClick = {
                                    alimentoWrapper.unit = "100g"
                                }
                            )

                            Text(stringResource(id = R.string._100g))

                            RadioButton(
                                selected = alimentoWrapper.unit == "unit",
                                onClick = {
                                    alimentoWrapper.unit = "unit"
                                }
                            )
                            Text(stringResource(id = R.string.unit))

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
                                    Text(text = stringResource(id = R.string.carboidrati))
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
                                    Text(text = stringResource(id = R.string.protein))
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
                                    Text(text = stringResource(id = R.string.fibre))
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
                                    Text(text = stringResource(id = R.string.calories))
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
                                    Text(text = stringResource(id = R.string.grassi))
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
                                    Text(text = stringResource(id = R.string.salt))
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
                                    Text(text = stringResource(id = R.string.description))
                                }
                            )
                        }


                    }

                }
            )

        }
    }


    /**
     * Gestisce la scansione del codice a barre:
     * quando viene riconosciuto un codice a barre, viene
     * gestita la chiamata al client foodopenfacts e viene
     * serializzato l'oggetto ricevuto dal client. Infine viene
     * utilizzato un oggetto di tipo AlimentoWrapper con proprieta' mutable,
     * per riempire automaticamente i campi che descrivono un alimento
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //val outputBarcode = view?.findViewById<TextView>(R.id.output_barcode)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                barcode = result.contents

                val toast = Toast.makeText(
                    requireContext(),
                    barcode + requireContext().getString(R.string.loadBarcode),
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
                            requireContext().getString(R.string.productNotFound),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.i("load error", "errore caricamento")
                        Log.i("load error", ex.toString())
                    }
                    toast.cancel()

                    if (product == null || product.status == 0) {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.productNotFound),
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
                                requireContext().getString(R.string.productNotFound),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }


    /**
     *Oggetto che viene utilizzato nella LazyColumn
     * per elencare tutti gli alimenti e le poro proprieta'
     */
    @Composable
    fun ItemFood(alimento: Alimento) {
        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
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
                                .height(48.dp)
                                .width(48.dp),
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
                                text = stringResource(id = R.string.kcal),
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
                                            label = stringResource(id = R.string.grassi),
                                            value = alimento.grassi?.toInt() ?: 0
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFF04724D),
                                            label = stringResource(id = R.string.fibre),
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
                                            label = stringResource(id = R.string.carboidrati),
                                            value = alimento.carboidrati?.toInt() ?: 0
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFFDB504A),
                                            label = stringResource(id = R.string.protein),
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
                contentDescription = "Add",
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


    /**
     * Oggetto AlimentoWrapper con proprieta' mutableStateOf
     */
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
            descrizione = ""
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



