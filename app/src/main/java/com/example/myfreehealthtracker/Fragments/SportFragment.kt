package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.LocalDatabase.Entities.Attivita
import com.example.myfreehealthtracker.LocalDatabase.Entities.Sport
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalDBViewModel
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalViewModelFactory
import com.example.myfreehealthtracker.MainApplication
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.DefaultDatePickerConfig
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.datepicker.data.model.SelectionLimiter
import com.vsnappy1.datepicker.ui.model.DatePickerConfiguration
import com.vsnappy1.timepicker.TimePicker
import com.vsnappy1.timepicker.ui.model.TimePickerConfiguration
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class SportFragment : Fragment() {
    private lateinit var mainApplication: MainApplication
    private val dbViewModel: InternalDBViewModel by viewModels {
        InternalViewModelFactory(
            mainApplication.userRepository,
            mainApplication.alimentoRepository,
            mainApplication.pastoRepository,
            mainApplication.pastoToCiboRepository,
            mainApplication.sportRepository,
            mainApplication.attivitaRepository
        )
    }
    private var openAddActivityDialog by mutableStateOf(false)
    private var openNewSportDialog by mutableStateOf(false)
    private var newSportWrapper = SportWrapper()
    private var newActivityWrapper = ActivityWrapper()

    private var pickedYear by mutableStateOf(0)
    private var pickedMonth by mutableStateOf(0)
    private var pickedDay by mutableStateOf(0)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mainApplication = requireActivity().application as MainApplication
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                Scaffold(content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        val activityList by dbViewModel.allAttivita.observeAsState(initial = emptyList())

                        if (activityList.isEmpty()) {
                            Text(text = "Nessuna attivita' presente")
                        } else {

                            BurnedCaloriesChart()
                            //MovimentChart(activityList)
                            //BurnCaloriesChart(activityList)
                            //WorkoutTimeChart(activityList)
                            ShowActivityList(activityList)
                        }
                        if (openAddActivityDialog) AddActivityDialog()
                        if (openNewSportDialog) NewSportDialog()
                    }
                }, floatingActionButton = {
                    IconButton(
                        onClick = { openAddActivityDialog = true },
                        modifier = Modifier
                            .background(color = Color.Green, CircleShape)
                            .size(60.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                })
            }
        }
    }

    @Composable
    private fun BurnedCaloriesChart() {

        val allAttivita by dbViewModel.allAttivita.observeAsState(initial = emptyList())


        val calendar = Calendar.getInstance()

        // Sottrai 5 giorni dalla data corrente
        calendar.add(Calendar.DAY_OF_YEAR, -4)

        // Imposta l'ora a mezzanotte (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Ottieni l'oggetto Date dal Calendar
        val date = calendar.time


        var filteredAttivita = allAttivita.filter {
            it.date >= date
        }.sortedBy { it.date }

        val listCalories = LinkedHashMap<Int, Double>()


        filteredAttivita.forEach {


            //take the week calories
            if (listCalories.containsKey(it.date.day)) {
                listCalories[it.date.day] = listCalories[it.date.day]!! + it.calorie
            } else listCalories[it.date.day] = it.calorie.toDouble()


        }

        val list = listCalories.values.toList()

        if (filteredAttivita != null && listCalories.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(10.dp)
            ) {

                LineChart(
                    data = listOf(
                        Line(
                            label = "Calorie bruciate negli ultimi giorni",
                            values = list,
                            color = SolidColor(Color(0xFFDB504A)),
                            //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                            firstGradientFillColor = Color(0xFFDB504A).copy(alpha = .7f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                            gradientAnimationDelay = 500,
                            drawStyle = DrawStyle.Stroke(width = 2.dp),
                            curvedEdges = false
                        ),
                    ),
                    animationMode = AnimationMode.Together(delayBuilder = {
                        it * 500L
                    }),
            dotsProperties = DotProperties(
                enabled = true,
                radius = 12f,
                color = SolidColor(Color(0xFF04724D)),
                strokeWidth = 6f,
                //strokeColor = Color.White,
                strokeStyle = StrokeStyle.Normal,
                animationEnabled = true,
                animationSpec = tween(500)
            ),
                    dividerProperties = DividerProperties(
                        enabled = false,
                        xAxisProperties = LineProperties(
                            enabled = false
                        ),
                        yAxisProperties = LineProperties(
                            enabled = false
                        )
                    ),

                    gridProperties = GridProperties(
                        enabled = false,
                    )
                )
            }
        }
    }

    @Composable
    private fun ShowActivityList(activityList: List<Attivita>) {
        LazyColumn() {
            items(activityList) {
                ShowActivityItem(it)
            }
        }
    }

    @Composable
    private fun ShowActivityItem(activity: Attivita) {
        var durata: Int = activity.durata
        var lunghezza: Int = activity.distanza
        var calorie: Int = activity.calorie
        val sport by mainApplication.sportRepository.getSportById(activity.idSport).asLiveData()
            .observeAsState()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    //NOME E NOTE
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    Text(
                        text = dateFormat.format(activity.date),
                        modifier = Modifier.weight(3f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = sport?.nomeSport ?: "",
                        modifier = Modifier.weight(1.2f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
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
                        //piechart con la somma dei valori nutrizionali
                        PieChart(
                            pieChartData = PieChartData(
                                listOf(
                                    PieChartData.Slice(
                                        durata.toFloat(), Color(0xFFE1E289)
                                    ),
                                    PieChartData.Slice(lunghezza.toFloat(), Color(0xFFDB504A)),
                                    PieChartData.Slice(calorie.toFloat(), Color(0xFF59C3C3)),
                                )
                            ),
                            modifier = Modifier.fillMaxSize(),
                            animation = simpleChartAnimation(),
                            sliceDrawer = SimpleSliceDrawer(sliceThickness = 15F)
                        )
                        Column()
                        {
                            Text(
                                text = "Andamento",
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
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
                                            label = "Calorie",
                                            value = calorie,
                                            suffix = "Kcal"
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
                                            label = "Durata",
                                            value = durata,
                                            suffix = "min"
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFFDB504A),
                                            label = "Lunghezza",
                                            value = lunghezza,
                                            suffix = "Km"
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
    private fun NewSportDialog() {

        AlertDialog(onDismissRequest = { openNewSportDialog = false }, confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    //Add new Sport
                    openNewSportDialog = false
                    //create id by Firebase
                    newSportWrapper.id =
                        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.SPORT).push()
                            .toString()
                            .removePrefix("https://my-free-health-tracker-default-rtdb.europe-west1.firebasedatabase.app/sport/")
                    if (newSportWrapper.id.isNotEmpty()) {
                        val sport = newSportWrapper.convertToSport()
                        lifecycleScope.launch {
                            //insert Room, DB
                            mainApplication.sportRepository.insertSport(sport)
                        }
                        //add firebase push new Sport
                        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.SPORT)
                            .child(sport.id).setValue(sport)
                    } else {
                        Toast.makeText(
                            requireContext(), "Nessun elemento inserito", Toast.LENGTH_LONG
                        ).show()
                    }

                }) {
                    Text(text = "Conferma")
                }
            }
        }, title = {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Inserisci Sport")
                IconButton(onClick = { openNewSportDialog = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Close Dialog")
                }
            }
        }, text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(value = newSportWrapper.nomeSport, onValueChange = {
                    newSportWrapper.nomeSport = it
                }, label = {
                    Text(text = "Nome")
                })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(modifier = Modifier.weight(1f),
                        value = newSportWrapper.descrizione,
                        onValueChange = {
                            newSportWrapper.descrizione = it
                        },
                        label = {
                            Text(text = "Descrizione")
                        })
                }
            }
        })
    }

    @Composable
    fun AddActivityDialog() {
        val existingSportList by dbViewModel.allSport.observeAsState(initial = emptyList())
        var selectedSportId by rememberSaveable { mutableStateOf("") }
        var selectedSportName by rememberSaveable { mutableStateOf("Seleziona Esistente") }
        var isDropDownMenuExpanded by rememberSaveable { mutableStateOf(false) }
        var showNewSportButton by rememberSaveable { mutableStateOf(true) }
        var openDetails by rememberSaveable { mutableStateOf(false) }
        var enableConferma by rememberSaveable { mutableStateOf(false) }
        var pickedHour by rememberSaveable { mutableIntStateOf(0) }
        var pickedMinute by rememberSaveable { mutableIntStateOf(0) }
        if (newActivityWrapper.calorie > 0 && newActivityWrapper.durata > 0 && newActivityWrapper.distanza >= 0)
            enableConferma = true
        AlertDialog(onDismissRequest = {
            openAddActivityDialog = false
        }, confirmButton = {
            Button(modifier = Modifier.fillMaxWidth(), enabled = enableConferma, onClick = {
                val selectedSport = existingSportList.find {
                    it.id == selectedSportId
                }
                if (selectedSport != null) {

                    val calendar = Calendar.getInstance()
                    calendar.set(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute)

                    newActivityWrapper.date = calendar.time
//                    newActivityWrapper.date.hours = pickedHour
//                    newActivityWrapper.date.minutes = pickedMinute
                    newActivityWrapper.idSport = selectedSport.id
                    newActivityWrapper.userId = mainApplication.userData!!.userData.value!!.id

                    val activity = newActivityWrapper.convertToAttivita()
                    //room DB push
                    lifecycleScope.launch {
                        mainApplication.attivitaRepository.insertAttivita(activity)
                    }
                    //firebase push
                    mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.ATTIVITA)
                        .child(activity.userId + activity.idSport + activity.date)
                        .setValue(activity)

                } else {
                    Toast.makeText(
                        requireContext(), "Nessuno sport selezionato", Toast.LENGTH_LONG
                    ).show()
                }
                openAddActivityDialog = false
            }) {
                Text(text = "Conferma")
            }
        }, title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Aggiungi Attività")
                IconButton(onClick = { openAddActivityDialog = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Close Dialog")
                }
            }
        }, text = {
            Column(
            ) {
                // SPINNER CON SPORT
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
                            Text(text = selectedSportName)
                            Icon(
                                Icons.Default.ArrowDropDown, contentDescription = null
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = isDropDownMenuExpanded, onDismissRequest = {
                            isDropDownMenuExpanded = false
                        }, modifier = Modifier.fillMaxHeight(0.5f)
                    ) {
                        existingSportList.forEach {
                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), text = {
                                //Text(text = it.nome ?: "ciao", modifier = Modifier.fillMaxWidth())
                                ItemSportSpinnerMenu(it)
                            }, onClick = {
                                selectedSportName = it.nomeSport
                                selectedSportId = it.id
                                isDropDownMenuExpanded = false
                                if (selectedSportName != "") {
                                    openDetails = true
                                    showNewSportButton = false
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Nessun Sport selezionato",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                        }
                    }
                }
                Button(
                    onClick = {
                        openAddActivityDialog = false

                        openNewSportDialog = true
                    }, enabled = showNewSportButton, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Nuovo Sport")
                }
                Box() {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {


                        val calendar = Calendar.getInstance()


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
                                .padding(16.dp)
                                .background(Color.Transparent, RoundedCornerShape(8.dp))
                                .height(100.dp),

                            onTimeSelected = { hour, minute ->
                                pickedMinute = minute
                                pickedHour = hour
                            },
                            configuration = TimePickerConfiguration.Builder()
                                .numberOfTimeRowsDisplayed(count = 3)
                                .selectedTimeScaleFactor(scaleFactor = 1.5f).build(),
                            is24Hour = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                enabled = openDetails,
                                value = newActivityWrapper.calorie.toString(),
                                onValueChange = {
                                    try {
                                        newActivityWrapper.calorie = it.toInt()
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Calorie Bruciate")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                                )
                            )
                            TextField(
                                modifier = Modifier.weight(1f),
                                enabled = openDetails,
                                value = newActivityWrapper.durata.toString(),
                                onValueChange = {
                                    try {
                                        newActivityWrapper.durata = it.toInt()
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Durata Totale")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
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
                                enabled = openDetails,
                                value = newActivityWrapper.distanza.toString(),
                                onValueChange = {
                                    try {
                                        newActivityWrapper.distanza = it.toInt()
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Distanza Totale")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                                )
                            )
                            TextField(modifier = Modifier.weight(1f),
                                enabled = openDetails,
                                value = newActivityWrapper.note,
                                onValueChange = {
                                    try {
                                        newActivityWrapper.note = it
                                    } catch (ex: Exception) {
                                    }

                                },
                                label = {
                                    Text(text = "Note")
                                }

                            )
                        }
                    }

                }
            }
        })
    }

    @Composable
    fun ItemSportSpinnerMenu(sport: Sport) {
        Box(
            modifier = Modifier.fillMaxWidth()

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
                    Text(
                        text = sport.nomeSport,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = sport.desc, fontSize = 15.sp
                    )
                }
                Box(
                    modifier = Modifier, contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = sport.image,
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
    private fun WorkoutTimeChart(activityList: List<Attivita>) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val dates = activityList.map { it.date.toString().format("dd/MM/yyyy") }
            val workoutTime = activityList.map { it.durata.toDouble() }
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp), data = listOf(
                    Line(
                        label = "Durata Allenamento",
                        values = workoutTime,
                        color = SolidColor(Color(0xFF04724D)),
                        //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                        firstGradientFillColor = Color(0xFF04724D).copy(alpha = .7f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                        gradientAnimationDelay = 500,
                        drawStyle = DrawStyle.Stroke(width = 2.dp)
                    )
                ), animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                dotsProperties = DotProperties(
                    enabled = false,
                    radius = 10f,
                    color = SolidColor(Color(0xFF04724D)),
                    strokeWidth = 3f,
                    //strokeColor = Color.White,
                    strokeStyle = StrokeStyle.Normal,
                    animationEnabled = true,
                    animationSpec = tween(500)
                ), dividerProperties = DividerProperties(
                    enabled = true, xAxisProperties = LineProperties(
                        enabled = false
                    ), yAxisProperties = LineProperties(
                        enabled = false
                    )
                ),

                gridProperties = GridProperties(
                    enabled = false,
                ),

                labelProperties = LabelProperties(
                    enabled = true, textStyle = MaterialTheme.typography.labelSmall,
                    //verticalPadding = 16.dp,
                    labels = dates
                )
            )

        }
    }

//    @Composable
//    private fun MovimentChart(activityList: List<Attivita>) {
//        val dates = activityList.map { it.date.toString().format("dd/MM/yyyy") }
//        val km = activityList.map { it.distanza.toDouble() }
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//        ) {
//            LineChart(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 4.dp), data = listOf(
//                    Line(
//                        label = "Distanza percorsa",
//                        values = km,
//                        color = SolidColor(Color(0xFF04724D)),
//                        //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
//                        firstGradientFillColor = Color(0xFF04724D).copy(alpha = .7f),
//                        secondGradientFillColor = Color.Transparent,
//                        strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
//                        gradientAnimationDelay = 500,
//                        drawStyle = DrawStyle.Stroke(width = 2.dp)
//                    )
//                ), animationMode = AnimationMode.Together(delayBuilder = {
//                    it * 500L
//                }),
//                dotsProperties = DotProperties(
//                    enabled = false,
//                    radius = 10f,
//                    color = SolidColor(Color(0xFF04724D)),
//                    strokeWidth = 3f,
//                    //strokeColor = Color.White,
//                    strokeStyle = StrokeStyle.Normal,
//                    animationEnabled = true,
//                    animationSpec = tween(500)
//                ), dividerProperties = DividerProperties(
//                    enabled = true, xAxisProperties = LineProperties(
//                        enabled = false
//                    ), yAxisProperties = LineProperties(
//                        enabled = false
//                    )
//                ),
//
//                gridProperties = GridProperties(
//                    enabled = false,
//                ),
//
//                labelProperties = LabelProperties(
//                    enabled = true, textStyle = MaterialTheme.typography.labelSmall,
//                    //verticalPadding = 16.dp,
//                    labels = dates
//                )
//            )
//        }
//    }
//
//    @Composable
//    private fun BurnCaloriesChart(activityList: List<Attivita>) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//        ) {
//            val dates = activityList.map { it.date.toString().format("dd/MM/yyyy") }
//            val burnCalories = activityList.map { it.calorie.toDouble() }
//            LineChart(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 4.dp), data = listOf(
//                    Line(
//                        label = "Calorie Bruciate",
//                        values = burnCalories,
//                        color = SolidColor(Color(0xFF04724D)),
//                        //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
//                        firstGradientFillColor = Color(0xFF04724D).copy(alpha = .7f),
//                        secondGradientFillColor = Color.Transparent,
//                        strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
//                        gradientAnimationDelay = 500,
//                        drawStyle = DrawStyle.Stroke(width = 2.dp)
//                    )
//                ), animationMode = AnimationMode.Together(delayBuilder = {
//                    it * 500L
//                }),
//
//                dotsProperties = DotProperties(
//                    enabled = false,
//                    radius = 10f,
//                    color = SolidColor(Color(0xFF04724D)),
//                    strokeWidth = 3f,
//                    //strokeColor = Color.White,
//                    strokeStyle = StrokeStyle.Normal,
//                    animationEnabled = true,
//                    animationSpec = tween(500)
//                ), dividerProperties = DividerProperties(
//                    enabled = true, xAxisProperties = LineProperties(
//                        enabled = false
//                    ), yAxisProperties = LineProperties(
//                        enabled = false
//                    )
//                ),
//                gridProperties = GridProperties(
//                    enabled = false,
//                ),
//                labelProperties = LabelProperties(
//                    enabled = true, textStyle = MaterialTheme.typography.labelSmall,
//                    //verticalPadding = 16.dp,
//                    labels = dates
//                )
//            )
//        }
//    }

//    @Composable
//    private fun Hello() {
//        Box(modifier = Modifier.padding(10.dp)) {
//            Text(
//                text = "I Progressi di " + mainApplication.userData!!.userData.value!!.nome,
//                fontSize = 50.sp,
//                textAlign = TextAlign.Center
//            )
//        }
//    }

    @Composable
    fun BulletSpan(color: Color, label: String, value: Int, suffix: String = "") {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
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
            text = "$label: ${value}+${suffix}"
        )
    }

    private class SportWrapper {

        var id: String by mutableStateOf("")
        var nomeSport: String by mutableStateOf("")

        var descrizione: String by mutableStateOf("")

        var enabled by mutableStateOf(true)


        fun convertToWrapper(sport: Sport) {
            id = sport.id
            nomeSport = sport.nomeSport
            descrizione = sport.desc
        }

        fun convertToSport(): Sport {
            return Sport(
                id, nomeSport, descrizione, ""
            )
        }

        fun loadFromString(s: String?) {
            if (s != null) {
                val split = s.split("|")
                id = split[0]
                nomeSport = split[1]
                descrizione = split[2]

            }
        }
    }

    private class ActivityWrapper {
        fun convertToAttivita(): Attivita {
            return Attivita(userId, idSport, date, note, calorie, durata, distanza)
        }

        var userId: String by mutableStateOf("")
        var idSport: String by mutableStateOf("")
        var date: Date by mutableStateOf(Date())
        var note: String by mutableStateOf("")
        var calorie: Int by mutableIntStateOf(0)
        var durata: Int by mutableIntStateOf(0)
        var distanza: Int by mutableIntStateOf(0)
    }
}

