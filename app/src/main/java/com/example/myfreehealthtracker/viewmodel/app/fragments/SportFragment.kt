package com.example.myfreehealthtracker.viewmodel.app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.ApplicationTheme
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.LocalDatabase.Entities.Attivita
import com.example.myfreehealthtracker.LocalDatabase.Entities.Sport
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalDBViewModel
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalViewModelFactory
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.utils.Actions
import com.example.myfreehealthtracker.utils.Clock
import com.example.myfreehealthtracker.utils.SportActivityService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

class SharedViewModel : ViewModel() {
    private val _counterValue = MutableLiveData(1) // Initial value
    val counterValue: LiveData<Int> = _counterValue

    fun updateCounterValue(newValue: Int) {
        _counterValue.value = newValue

    }

    //for some reason don't work
    fun getValue(): LiveData<Int> {
        Log.i("test", counterValue.value.toString())
        return counterValue
    }
}

class MyBroadCastReceiver(private val sharedViewModel: SharedViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val counterValue = intent?.getIntExtra(SportActivityService.EXTRA_COUNTER_VALUE, 0) ?: 0
        sharedViewModel.updateCounterValue(counterValue)
    }

}
@Suppress("DEPRECATION")
class SportFragment : Fragment() {
    private lateinit var mainApplication: MainApplication
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
    private var openAddActivityDialog by mutableStateOf(false)
    private var openNewSportDialog by mutableStateOf(false)
    private var newSportWrapper = SportWrapper()
    private var newActivityWrapper = ActivityWrapper()
    private var fireBaseSport by mutableStateOf(mutableListOf<Sport>())
    private var pickedYear by mutableIntStateOf(0)
    private var pickedMonth by mutableIntStateOf(0)
    private var pickedDay by mutableIntStateOf(0)
    private var clock = Clock()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var counterUpdateReceiver: MyBroadCastReceiver

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SportActivityService.ACTION_COUNTER_UPDATE)
        requireContext().registerReceiver(
            counterUpdateReceiver,
            intentFilter,
            Activity.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(counterUpdateReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mainApplication = requireActivity().application as MainApplication
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        lifecycleScope.launch {
            retriveSportFromFirebase()
        }
        counterUpdateReceiver = MyBroadCastReceiver(sharedViewModel)

        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            mainApplication.userData!!.userData.observe(viewLifecycleOwner) {
                if (mainApplication.userData!!.userData.value != null) {
                    setContent {
                        ApplicationTheme {
                            Scaffold(content = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    val activityList by dbViewModel.allAttivita.observeAsState(
                                        initial = emptyList()
                                    )
                                    if (activityList.isEmpty()) {
                                        Text(text = stringResource(id = R.string.noActivityPresent))
                                    } else {
                                        BurnedCaloriesChart()
                                        ShowActivityList(activityList)
                                    }
                                    if (openAddActivityDialog) AddActivityDialog()
                                    if (openNewSportDialog) NewSportDialog()
                                }
                            }, floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { openAddActivityDialog = true },
                                    modifier = Modifier
                                        .size(56.dp),
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                                }
                            })
                        }
                    }
                }
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


        val filteredAttivita = allAttivita.filter {
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
                            label = stringResource(id = R.string.burnCaloriesLastDay),
                            values = list,
                            color = SolidColor(MaterialTheme.colorScheme.primary),
                            //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                            firstGradientFillColor = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
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
                        color = SolidColor(MaterialTheme.colorScheme.primary),
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
        LazyColumn {
            items(activityList) {
                ShowActivityItem(it)
            }
        }
    }

    @Composable
    private fun ShowActivityItem(activity: Attivita) {
        val durata: Int = activity.durata
        val lunghezza: Int = activity.distanza
        val calorie: Int = activity.calorie
        val sport by dbViewModel.loadSportById(activity.idSport).observeAsState(initial = null)
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
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = stringResource(id = R.string.calories))
                                Text(text = "$calorie Kcal", fontWeight = FontWeight.Medium)
                            }

                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(1.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = stringResource(id = R.string.timeTotal))
                                Text(text = "$durata min", fontWeight = FontWeight.Medium)
                            }

                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(1.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = stringResource(id = R.string.totalLenght))
                                Text(text = "$lunghezza km", fontWeight = FontWeight.Medium)
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
                        dbViewModel.insertSport(sport)
                        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.SPORT)
                            .child(sport.id).setValue(sport)
                        val bundle = Bundle().apply {
                            putString("id", mainApplication.userData!!.userData.value!!.id)
                        }
                        firebaseAnalytics.logEvent("hasInsertedSport", bundle)

                        //add firebase push new Sport

                    } else {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.noElementInserted),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }, title = {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.addSport))
                IconButton(onClick = { openNewSportDialog = false }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.closeDrawer)
                    )
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
                    Text(text = stringResource(id = R.string.name))
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
                            Text(text = stringResource(id = R.string.description))
                        })
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun AddActivityDialog() {
        val existingSportList by dbViewModel.allSport.observeAsState(initial = emptyList())

        var selectedSportId by rememberSaveable { mutableStateOf("") }
        var selectedSportName by rememberSaveable { mutableStateOf(requireContext().getString(R.string.selectExisted)) }
        var selectedSportNameFirebase by rememberSaveable {
            mutableStateOf(
                requireContext().getString(
                    R.string.selectExistedFirebase
                )
            )
        }
        val sportFiltered = fireBaseSport - existingSportList
        var blockDate by rememberSaveable { mutableStateOf(false) }
        var isDropDownMenuExpanded by rememberSaveable { mutableStateOf(false) }
        var enableInsertDuration by rememberSaveable { mutableStateOf(true) }
        var enableModify by rememberSaveable { mutableStateOf(true) }
        var isDropDownMenuFirebaseExpanded by rememberSaveable { mutableStateOf(false) }
        var showFirebaseSport by rememberSaveable { mutableStateOf(true) }
        var showInternalSport by rememberSaveable { mutableStateOf(true) }
        var showNewSportButton by rememberSaveable { mutableStateOf(true) }
        var openDetails by rememberSaveable { mutableStateOf(false) }
        var enableConferma by rememberSaveable { mutableStateOf(false) }
        var enableInsertManually by rememberSaveable { mutableStateOf(true) }
        var pickedHour by rememberSaveable { mutableIntStateOf(0) }
        var pickedMinute by rememberSaveable { mutableIntStateOf(0) }
        var showAddActivity by rememberSaveable {
            mutableStateOf(false)
        }
        var clockEnable by rememberSaveable {
            mutableStateOf(false)
        }

        if (clockEnable) {
            clock.start()
        } else {
            clock.stop()
        }
        val counterValue by clock.getValue().asLiveData().observeAsState(
            initial = 0
        )
        Log.i("test", "internal class value" + counterValue.toString())
        if (newActivityWrapper.calorie > 0 && newActivityWrapper.durata > 0 && newActivityWrapper.distanza >= 0)
            enableConferma = true
        AlertDialog(onDismissRequest = {

            if (isServiceRunning(requireContext(), SportActivityService::class.java)) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.stopServiceBefore),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                openAddActivityDialog = false
            }
        }, confirmButton = {
            Button(modifier = Modifier.fillMaxWidth(), enabled = enableConferma, onClick = {
                val selectedSport = existingSportList.find {
                    it.id == selectedSportId
                }
                if (selectedSport != null) {

                    val calendar = Calendar.getInstance()
                    calendar.set(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute)

                    newActivityWrapper.date = calendar.time
                    newActivityWrapper.idSport = selectedSport.id
                    newActivityWrapper.userId = mainApplication.userData!!.userData.value!!.id
                    val activity = newActivityWrapper.convertToAttivita()
                    //room DB push
                    dbViewModel.insertAttivita(activity)
                    //firebase push
                    mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.ATTIVITA)
                        .child(activity.userId + activity.idSport + activity.date)
                        .setValue(activity)
                    val bundle = Bundle().apply {
                        putString("id", mainApplication.userData!!.userData.value!!.id)
                    }
                    firebaseAnalytics.logEvent("hasInsertedActivity", bundle)


                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.noSportSelected),
                        Toast.LENGTH_LONG
                    ).show()
                }
                openAddActivityDialog = false
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }, title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.addActivity))
                IconButton(onClick = {

                    if (isServiceRunning(requireContext(), SportActivityService::class.java)) {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.stopServiceBefore),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        openAddActivityDialog = false
                    }
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.closeDrawer)
                    )
                }
            }
        }, text = {
            Column()
            {
                // SPINNER CON SPORT
                Box {
                    Button(
                        enabled = showInternalSport,
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
                                ItemSportSpinnerMenu(it)
                            }, onClick = {
                                selectedSportName = it.nomeSport
                                selectedSportId = it.id
                                isDropDownMenuExpanded = false
                                if (selectedSportName != "") {
                                    openDetails = true
                                    showNewSportButton = false
                                    showFirebaseSport = false
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        requireContext().getString(R.string.noSportSelected),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                        }
                    }
                }
                Box {
                    Button(
                        enabled = showFirebaseSport,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            isDropDownMenuFirebaseExpanded = !isDropDownMenuFirebaseExpanded
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedSportNameFirebase)
                            Icon(
                                Icons.Default.ArrowDropDown, contentDescription = ""
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = isDropDownMenuFirebaseExpanded, onDismissRequest = {
                            isDropDownMenuFirebaseExpanded = false
                        }, modifier = Modifier.fillMaxHeight(0.5f)
                    ) {
                        sportFiltered.forEach {
                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), text = {
                                ItemSportSpinnerMenu(it)
                            }, onClick = {
                                selectedSportNameFirebase = it.nomeSport
                                selectedSportId = it.id
                                isDropDownMenuFirebaseExpanded = false
                                if (selectedSportNameFirebase != "") {
                                    openDetails = true
                                    showNewSportButton = false
                                    showInternalSport = false

                                    //if not present try to push in internal db
                                    dbViewModel.insertSport(it)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        requireContext().getString(R.string.noSportSelected),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                        }
                    }
                }
                if (enableModify) {
                    Button(
                        onClick = {
                            openAddActivityDialog = false

                            openNewSportDialog = true
                        }, enabled = showNewSportButton, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.newSport))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    var text by rememberSaveable { mutableStateOf(requireContext().getString(R.string.startActivity)) }
                    Button(
                        enabled = openDetails,
                        onClick = {
                            if (!isServiceRunning(
                                    requireContext(),
                                    SportActivityService::class.java
                                )
                            ) {
                                clockEnable = true
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().getString(R.string.startIncoming),
                                    Toast.LENGTH_LONG
                                ).show()

                                text = requireContext().getString(R.string.stopActivity)
                                showAddActivity = false
                                enableInsertManually = false
                                loadService()
                            } else {


                                newActivityWrapper.durata =
                                    ((counterValue + 16) / 6) / 60 //compensate for time error
                                Log.i("test", "durata" + (counterValue + 16) / 6)
                                val serviceIntent =
                                    Intent(context, SportActivityService::class.java).also {
                                        it.action = Actions.STOP.toString()
                                    }
                                clockEnable = false
                                requireContext().startService(serviceIntent)
                                showAddActivity = true
                                enableModify = false
                                enableInsertDuration = false
                                blockDate = true
                            }

                        }, modifier = Modifier.fillMaxWidth()
                    ) {


                        Text(text = text)
                    }
                    Button(
                        enabled = openDetails && enableInsertManually,
                        onClick = {
                            showAddActivity = true
                            enableModify = false
                        }, modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(text = stringResource(id = R.string.openAddActivity))
                    }
                }

                if (showAddActivity) {
                    Box() {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {


                            val calendar = Calendar.getInstance()

                            if (!blockDate) {
                                DatePicker(

                                    onDateSelected = { year, month, day ->
                                        pickedYear = year
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
                                        .selectedDateTextStyle(
                                            textStyle = TextStyle(
                                                Color(
                                                    0xFFFFFFFF
                                                )
                                            )
                                        )
                                        .selectedDateBackgroundColor(color = MaterialTheme.colorScheme.primary)
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
                            } else {
                                pickedYear = calendar.get(Calendar.YEAR)
                                pickedMonth = calendar.get(Calendar.MONTH)
                                pickedDay = calendar.get(Calendar.DAY_OF_MONTH)
                                pickedHour = calendar.get(Calendar.HOUR_OF_DAY)
                                pickedMinute = calendar.get(Calendar.MINUTE)
                            }
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
                                        Text(text = stringResource(id = R.string.caloriesBurn))
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )
                                )
                                TextField(
                                    modifier = Modifier.weight(1f),
                                    enabled = openDetails && enableInsertDuration,
                                    value = newActivityWrapper.durata.toString(),
                                    onValueChange = {
                                        try {
                                            newActivityWrapper.durata = it.toInt()
                                        } catch (ex: Exception) {
                                        }

                                    },
                                    label = {
                                        Text(text = stringResource(id = R.string.timeTotal))
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
                                    enabled = openDetails,
                                    value = newActivityWrapper.distanza.toString(),
                                    onValueChange = {
                                        try {
                                            newActivityWrapper.distanza = it.toInt()
                                        } catch (ex: Exception) {
                                        }

                                    },
                                    label = {
                                        Text(text = stringResource(id = R.string.totalLenght))
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
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
                                        Text(text = stringResource(id = R.string.note))
                                    }

                                )
                            }
                        }

                    }
                }
            }
        })
    }

    private fun loadService() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                            Manifest.permission.FOREGROUND_SERVICE,
                            Manifest.permission.POST_NOTIFICATIONS
                        ),

                        100
                    )
                }
            }
        } else {
            val sportActivityService = Intent(context, SportActivityService::class.java).also {
                it.action = Actions.START.toString()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(sportActivityService)
            } else {
                requireContext().startService(sportActivityService)
            }
        }
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
                        label = stringResource(id = R.string.timeActivity),
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
                contentDescription = "Add",
                modifier = Modifier
                    .width(24.dp)
                    .background(color, CircleShape)
                    .padding(2.dp),
                tint = color
            )
        }
        Text(
            text = "$label: $value $suffix"
        )
    }


    private fun retriveSportFromFirebase() {
        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.SPORT).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (sportSnapshot in dataSnapshot.children) {
                    val sport = sportSnapshot.getValue(Sport::class.java)
                    sport?.let {
                        fireBaseSport.add(it)
                    }
                }

                // Now you have a list of User objects
                Log.i("SportFragment", "load sport from firebase")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private class SportWrapper {

        var id: String by mutableStateOf("")
        var nomeSport: String by mutableStateOf("")

        var descrizione: String by mutableStateOf("")


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

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}

