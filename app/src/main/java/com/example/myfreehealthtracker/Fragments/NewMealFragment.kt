@file:Suppress("DEPRECATION")

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
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.zxing.integration.android.IntentIntegrator
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.DefaultDatePickerConfig
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.datepicker.data.model.SelectionLimiter
import com.vsnappy1.datepicker.ui.model.DatePickerConfiguration
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
    private val pastoToAlimentoWrapperList = mutableStateListOf<PastoToAlimentoWrapper>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val dbViewModel: InternalDBViewModel by viewModels {
        InternalViewModelFactory(
            mainApplication.alimentoRepository,
            mainApplication.pastoRepository,
            mainApplication.pastoToCiboRepository,
            mainApplication.sportRepository,
            mainApplication.attivitaRepository
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
            firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        } catch (ex: Exception) {
            Log.i("NewFoodFragment", "MainApp error")
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.dbConnectionError),
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
                    text = stringResource(id = R.string.createNewPasto),
                    fontSize = 24.sp
                )
                IconButton(
                    onClick = {
                        showAddFoodDialog = true
                    },
                    modifier = Modifier.background(Color.Green, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
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
                Text(text = stringResource(id = R.string.confirmInsert))
            }
        }

        if (openInsertDialog) {
            var pickedHour by remember { mutableIntStateOf(0) }
            var pickedMinute by remember { mutableIntStateOf(0) }
            var selectMealType by remember { mutableStateOf(requireContext().getString(R.string.selectTypePasto)) }
            var pickedYear by remember { mutableIntStateOf(0) }
            var pickedMonth by remember { mutableIntStateOf(0) }
            var pickedDay by remember { mutableIntStateOf(0) }
            var tipoPasto: TipoPasto = TipoPasto.Spuntino
            var openDropDownMenu by remember {
                mutableStateOf(false)
            }
            val calendar = Calendar.getInstance()
            pickedHour = calendar.get(Calendar.HOUR_OF_DAY)
            pickedMinute = calendar.get(Calendar.MINUTE)
            AlertDialog(onDismissRequest = { openInsertDialog = false }, confirmButton = {

            }, title = {
                Text(text = stringResource(id = R.string.insertDataPasto))
            },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {




                        DatePicker(
                            onDateSelected = { year, month, day ->
                                pickedYear= year
                                pickedMonth = month
                                pickedDay = day

                            },
                            configuration = DatePickerConfiguration.Builder()
                                .height(height = 300.dp)
                                .dateTextStyle(
                                    DefaultDatePickerConfig.dateTextStyle.copy(
                                        color = Color(
                                            0xFF333333
                                        )
                                    )
                                )
                                .selectedDateTextStyle(textStyle = TextStyle(Color(0xFFFFFFFF)))
                                .selectedDateBackgroundColor(color = Color(0xFF64DD17))
                                .numberOfMonthYearRowsDisplayed(5)
                                .build(),
                            selectionLimiter = SelectionLimiter(

                                toDate = DatePickerDate(
                                    year = calendar.get(Calendar.YEAR),
                                    month = calendar.get(Calendar.MONTH),
                                    day = calendar.get(Calendar.DAY_OF_MONTH)
                                )
                            )
                        )


                        TimePicker(
                            modifier = Modifier
                                //.padding(16.dp)
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
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
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
                                        DropdownMenuItem(
                                            text = { Text(text = value.name) },
                                            onClick = {
                                                selectMealType = value.name
                                                tipoPasto = value
                                                openDropDownMenu = false
                                            })
                                    }
                                }
                            }
                        }
                        Button(
                            enabled = canConfirmMeal && selectMealType != stringResource(id = R.string.selectTypePasto),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                openInsertDialog = false
                                insertPasto(pickedMinute, pickedHour, pickedDay, pickedMonth, pickedYear, tipoPasto)

                            }) {
                            Text(text = stringResource(id = R.string.confirmInsert))

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

    private fun insertPasto(pickedMinute: Int, pickedHour: Int,pickedDay: Int, pickedMonth: Int, pickedYear: Int, tipoPasto: TipoPasto) {
        val calendar = Calendar.getInstance()
        calendar.set(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute)

        // Ottieni l'oggetto Date dal Calendar
        val date = calendar.time
        val pasto = Pasto(
            mainApplication.userData!!.userData.value!!.id,
            date,
            tipoPasto,
            ""
        ) //TODO TO CHECK
        dbViewModel.insertPasto(pasto)
        insertPastoToAlimento(pasto)
        val bundle = Bundle().apply {
            putString("id", mainApplication.userData!!.userData.value!!.id)
        }
        firebaseAnalytics.logEvent("hasInsertedMeal", bundle)

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
                dbViewModel.insertPastoToCibo(pastoToCibo)
                //push firebase db
                mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.PASTO)
                    .child(pastoToCibo.userID + pastoToCibo.date).child(pastoToCibo.idAlimento)
                    .setValue(pastoToCibo)
            }
            view?.findNavController()?.navigate(R.id.healthFragment)

        }
    }

    @Composable
    private fun ItemFoodInList(alimento: Alimento) {
        val pastoToCiboWrapper = pastoToAlimentoWrapperList.find {
            it.idAlimento == alimento.id
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                        Column() {

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
                            Text(text = stringResource(R.string.quantit) + { alimento.unit })
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Column(
                    //NOME E BARCODE
                    modifier = Modifier.weight(1f),

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

    @Composable
    private fun AddFoodDialog() {
        val existingAllAlimentList by dbViewModel.allAlimento.observeAsState(initial = emptyList())
        var selectedFoodItemBarcode by rememberSaveable { mutableStateOf("") }
        var selectedFoodItemLabel by rememberSaveable { mutableStateOf(requireContext().getString(R.string.selectExisted)) }
        var isDropDownMenuExpanded by rememberSaveable { mutableStateOf(false) }
        var enableNewFood by rememberSaveable {
            mutableStateOf(true)
        }
        var enableConferma by rememberSaveable {
            mutableStateOf(false)
        }
        AlertDialog(
            onDismissRequest = {
                showAddFoodDialog = false
            },
            confirmButton = {
                Button(
                    enabled = enableConferma,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val selectedFood = existingAllAlimentList.find {
                            it.id == selectedFoodItemBarcode
                        }
                        if (selectedFood != null) {
                            currentMealFoodList.add(selectedFood)
                            pastoToAlimentoWrapperList.add(
                                PastoToAlimentoWrapper(
                                    idUser = mainApplication.userData!!.userData.value!!.id, //TODO
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
                                requireContext().getString(R.string.noElementInserted),
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
                    Text(text = stringResource(id = R.string.addAlimento))
                    IconButton(onClick = { showAddFoodDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            text = {
                Column()
                {
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
                                        enableNewFood = false
                                        enableConferma = true
                                    }
                                )
                            }
                        }
                    }
                    Button(
                        enabled = enableNewFood,
                        onClick = {
                            showAddFoodDialog = false
                            alimentoWrapper = AlimentoWrapper()
                            showNewFoodDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string._new))
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
                        integrator.setPrompt(requireContext().getString(R.string.scanABarcode)) // Testo mostrato sopra l'area di scansione
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
                        Text(text = stringResource(id = R.string.scanner))
                    }
                    Button(
                        onClick = {
                            showNewFoodDialog = false

                            if (alimentoWrapper.id.isNotEmpty()) {
                                val food = alimentoWrapper.convertToFood()

                                currentMealFoodList.add(food)
                                canConfirmMeal = true

                                dbViewModel.insertAlimento(food)
                                val bundle = Bundle().apply {
                                    putString("id", mainApplication.userData!!.userData.value!!.id)
                                }
                                firebaseAnalytics.logEvent("hasInsertedFood", bundle)

                                pastoToAlimentoWrapperList.add(
                                    PastoToAlimentoWrapper(
                                        idUser = mainApplication.userData!!.userData.value!!.id,
                                        idAlimento = food.id,
                                        nomeAlimento = food.nome,
                                        imageUri = food.immagine,
                                        quantita = mutableStateOf("1")
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().getString(R.string.noElementInserted),
                                    Toast.LENGTH_LONG
                                ).show()
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