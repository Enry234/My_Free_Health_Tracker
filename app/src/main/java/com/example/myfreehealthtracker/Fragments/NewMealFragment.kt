package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import com.example.myfreehealthtracker.LocalDatabase.Entities.PastoToCibo
import com.example.myfreehealthtracker.LocalDatabase.Entities.TipoPasto
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
import com.vsnappy1.timepicker.TimePicker
import com.vsnappy1.timepicker.ui.model.TimePickerConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {

    private val currentMealFoodList = mutableStateListOf<Alimento>()
    private var alimentoWrapper = AlimentoWrapper()
    val pastoToAlimentoWrapperList = mutableStateListOf<PastoToAlimentoWrapper>()

    private val alimentoViewModel: InternalDBViewModel by viewModels {
        InternalViewModelFactory(
            mainApplication.userRepo,
            mainApplication.alimentoRepo,
            mainApplication.pastoRepo,
            mainApplication.pastoToCiboRepo
        )
    }


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

    @SuppressLint("DefaultLocale")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AddMealScreen() {
        var openInsertDialog by remember { mutableStateOf(false) }

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
                modifier = Modifier.weight(1f)
            ) {
                items(currentMealFoodList) {
                    ItemFoodInList(it)
                }
            }
            Button(
                enabled = canConfirmMeal,
                onClick = {
                    openInsertDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Conferma Inserimento")
            }
        }

        if (openInsertDialog) {
            val context = LocalContext.current

            var pickedHour by remember { mutableIntStateOf(0) }
            var pickedMinute by remember { mutableIntStateOf(0) }
            var selectMealType by remember { mutableStateOf("Seleziona tipo pasto") }
            var tipoPasto: TipoPasto = TipoPasto.Spuntino
            var openDropDownMenu by remember {
                mutableStateOf(false)
            }
            val calendar = Calendar.getInstance()
            pickedHour = calendar.get(Calendar.HOUR_OF_DAY)
            pickedMinute = calendar.get(Calendar.MINUTE)
            AlertDialog(onDismissRequest = { openInsertDialog = false }, confirmButton = {

            }, title = {
                Text(text = "Inserisci dati del pasto")
            },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                            //.fillMaxHeight(0.4f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        //Text(text = String.format("%02d:%02d", pickedHour, pickedMinute))
//                        Row() {
//                            Button(onClick = { isTimePickerDialogOpen = true }) {
//                            Text("Ins")
//                         }
//                        }
                        Text(text = "Scegli l'orario")
                        TimePicker(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(Color.Transparent, RoundedCornerShape(8.dp))
                                .height(100.dp),
                            onTimeSelected = { hour, minute ->
                                pickedMinute = minute
                                pickedHour = hour
                            },
                            configuration = TimePickerConfiguration.Builder()
                                .numberOfTimeRowsDisplayed(count = 3)
                                .selectedTimeScaleFactor(scaleFactor = 1.5f)
                                .build(),
                            is24Hour = true
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { openDropDownMenu = true }
                            ) {
                                Text(selectMealType)
                            }
                            Box(
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                DropdownMenu(
                                    expanded = openDropDownMenu,
                                    onDismissRequest = { openDropDownMenu = false }
                                ) {
                                    TipoPasto.entries.forEach { value ->
                                        DropdownMenuItem(text = { Text(text = value.name) }, onClick = {
                                            selectMealType = value.name
                                            tipoPasto = value
                                            openDropDownMenu = false
                                        })
                                    }
                                }
                            }
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                openInsertDialog = false
                                insertPasto(pickedMinute, pickedHour, tipoPasto)

                            }) {
                            Text(text = "Conferma Inserimento")

                        }
                    }
                })
        }


        if (showAddFoodDialog) {
            AddFoodDialog()
        } else if (showNewFoodDialog) {
            NewFoodDialog()
        }
    }

    private fun insertPasto(pickedMinute: Int, pickedHour: Int, tipoPasto: TipoPasto) {
        val date = Date()
        date.hours = pickedHour
        date.minutes = pickedMinute
        val pasto = Pasto(mainApplication.userData!!.id, date, tipoPasto, "")
        alimentoViewModel.insert(pasto)
        insertPastoToAlimento(pasto)
        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.PASTO)
            .child(pasto.userID + pasto.date).setValue(pasto)
    }

    private fun insertPastoToAlimento(pasto: Pasto) {
        lifecycleScope.launch {
            pastoToAlimentoWrapperList.forEach {
                val foodUnit = currentMealFoodList.find { alimento ->
                    alimento.id == it.idAlimento
                }?.unit

                if (foodUnit.equals("100g")) {
                    it.quantita.value = (it.quantita.value.toFloat() / 100f).toString()
                }
                it.idDate = pasto.date
                val pastoToCibo =
                    PastoToCibo(it.idUser, it.idDate, it.idAlimento, it.quantita.value.toFloat())
                //push internal db
                mainApplication.pastoToCiboRepo.insertPastoToCibo(pastoToCibo)
                //push firebase db
                mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.PASTO)
                    .child(pastoToCibo.userID + pastoToCibo.date).child(pastoToCibo.idAlimento)
                    .setValue(pastoToCibo)
            }
            view?.findNavController()?.navigate(R.id.healthFragment)

        }
    }

    private @Composable
    fun ItemFoodInList(alimento: Alimento) {
        val pastoToCiboWrapper = pastoToAlimentoWrapperList.find {
            it.idAlimento == alimento.id
        }
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
                            text = it,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = pastoToCiboWrapper?.quantita?.value ?: "",
                        onValueChange = {
                            try {
                                pastoToCiboWrapper?.quantita?.value = it.toFloat().toString()
                            } catch (ex: Exception) {

                            }
                        },
                        label = {
                            Text(text = "Quantità (${alimento.unit})")
                        },
                        keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )


                    IconButton(
                        onClick = {
                            currentMealFoodList.remove(alimento)
                            pastoToAlimentoWrapperList.remove(pastoToCiboWrapper)
                            if (currentMealFoodList.isEmpty()) {
                                canConfirmMeal = false
                            }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }

                }
            }
        }


    }

    @Composable
    fun ItemFoodSpinnerMenu(alimento: Alimento) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .border(
//                    width = 2.dp,
//                    color = Color.Black,
//                    shape= RoundedCornerShape(8.dp)
//                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                //verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    //NOME E BARCODE
                    modifier = Modifier.weight(1f),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    alimento.nome?.let {
                        Text(
                            text = it,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = alimento.id,
                        fontSize = 15.sp
                    )


                }

                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
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

    @Composable
    private fun AddFoodDialog() {
        val existingAllAlimentList by alimentoViewModel.allAlimento.observeAsState(initial = emptyList())
        var selectedFoodItemBarcode by rememberSaveable { mutableStateOf("") }
        var selectedFoodItemLabel by rememberSaveable { mutableStateOf("Seleziona Esistente") }
        var isDropDownMenuExpanded by rememberSaveable { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = {
                showAddFoodDialog = false
            },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val selectedFood = existingAllAlimentList.find {
                            it.id == selectedFoodItemBarcode
                        }
                        if (selectedFood != null) {
                            currentMealFoodList.add(selectedFood)
                            pastoToAlimentoWrapperList.add(
                                PastoToAlimentoWrapper(
                                    idUser = mainApplication.userData!!.id,
                                    idAlimento = selectedFood.id,
                                    nomeAlimento = selectedFood.nome,
                                    imageUri = selectedFood.immagine,
                                    quantita = mutableStateOf("1")
                                )
                            )
                            canConfirmMeal = true
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Nessun elemento selezionato",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        showAddFoodDialog = false
                    }
                ) {
                    Text(text = "Conferma")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Aggiungi Alimento")
                    IconButton(onClick = { showAddFoodDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            text = {
                Column(

                ) {
                    // SPINNER CON ALIMENTI ESISTENTI DA
                    Box {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                isDropDownMenuExpanded = !isDropDownMenuExpanded
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = selectedFoodItemLabel)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = isDropDownMenuExpanded,
                            onDismissRequest = {
                                isDropDownMenuExpanded = false
                            },
                            modifier = Modifier.fillMaxHeight(0.5f)
                        ) {
                            existingAllAlimentList.forEach {
                                DropdownMenuItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = {
                                        //Text(text = it.nome ?: "ciao", modifier = Modifier.fillMaxWidth())
                                        ItemFoodSpinnerMenu(it)
                                    },
                                    onClick = {
                                        selectedFoodItemLabel = it.nome ?: ""
                                        selectedFoodItemBarcode = it.id
                                        isDropDownMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            showAddFoodDialog = false
                            alimentoWrapper = AlimentoWrapper()
                            showNewFoodDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
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

                                currentMealFoodList.add(food)
                                canConfirmMeal = true

                                lifecycleScope.launch {
                                    mainApplication.alimentoDao.insertAlimento(food)
                                }

                                pastoToAlimentoWrapperList.add(
                                    PastoToAlimentoWrapper(
                                        idUser = mainApplication.userData!!.id,
                                        idAlimento = food.id,
                                        nomeAlimento = food.nome,
                                        imageUri = food.immagine,
                                        quantita = mutableStateOf("1")
                                    )
                                )
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

    data class PastoToAlimentoWrapper(
        var idAlimento: String = "",
        var idDate: Date = Date(),
        var idUser: String = "",
        var nomeAlimento: String? = "",
        var imageUri: String? = "",
        var quantita: MutableState<String> = mutableStateOf("")
    )

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