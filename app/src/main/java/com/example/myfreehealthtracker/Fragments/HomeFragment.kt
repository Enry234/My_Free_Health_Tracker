@file:Suppress("DEPRECATION")

package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myfreehealthtracker.ApplicationTheme
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalDBViewModel
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.InternalViewModelFactory
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.google.firebase.analytics.FirebaseAnalytics
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.min


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var mainApplication: MainApplication
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var proteine = 0f
    var carboidrati = 0f
    var grassi = 0f
    var fibre = 0f
    var calorie = 0f


    private var showUpdateWeightDialog by mutableStateOf(false)

    private val dbViewModel: InternalDBViewModel by viewModels {
        InternalViewModelFactory(
            mainApplication.alimentoRepository,
            mainApplication.pastoRepository,
            mainApplication.pastoToCiboRepository,
            mainApplication.sportRepository,
            mainApplication.attivitaRepository
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApplication = requireActivity().application as MainApplication
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        mainApplication.userData!!.userData.observe(viewLifecycleOwner) {
            if (mainApplication.userData!!.userData.value != null) {
                // Update the UI, in this case, a TextView.
                val bundle = Bundle().apply {
                    putString("id", mainApplication.userData!!.userData.value!!.id)
                }
                firebaseAnalytics.logEvent("login", bundle)
                composeView.setContent {

                    ApplicationTheme {


                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                        ) {

                            if (showUpdateWeightDialog) updateWeightDialog()
                            5

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                            ) {

                                Surface(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Box(
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        Row(

                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                            ) {
                                                DailyReport()
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(Color.White),
                                            ) {
                                                FoodRanking()
                                            }

                                        }

                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    tonalElevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Box(
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        DisplayMacros()

                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    tonalElevation = 8.dp,
                                    //shadowElevation = 16.dp,
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Box(
                                        modifier = Modifier.padding(8.dp)
                                        //.fillMaxWidth()
                                    ) {
                                        WeightChart()


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
    private fun updateWeightDialog() {

        val context = LocalContext.current
        var peso by remember { mutableStateOf("") }

        AlertDialog(onDismissRequest = { showUpdateWeightDialog = false }, confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Button(modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = {
                        val parsedPeso = peso.toDoubleOrNull()

                        if (parsedPeso != null) {

                            val bundle = Bundle().apply {
                                putString(
                                    "id", mainApplication.userData!!.userData.value!!.id
                                )
                            }
                            firebaseAnalytics.logEvent("hasUpdatePeso", bundle)
                            mainApplication.userData!!.userData.value!!.peso!!.add(
                                Pair(
                                    Date(), parsedPeso
                                )
                            )
                            //mainApplication.applicationScope.launch {
                            runBlocking {
                                mainApplication.userRepository.insertUser(
                                    mainApplication.userData!!.userData.value!!
                                )//upsert mode
                            }
                            //}
                            mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.USERS)
                                .child(mainApplication.userData!!.userData.value!!.id)
                                .setValue(mainApplication.userData!!)

                            showUpdateWeightDialog = false
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.errorPeso),
                                Toast.LENGTH_SHORT).show()
                        }

                    }) {
                    Text(text = stringResource(id = R.string.updatePeso))
                }
            }
        },
            text = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = peso,
                    label = { Text(text = stringResource(id = R.string.inserisciPeso)) },
                    onValueChange = { peso = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }

    @Composable
    private fun FoodRanking() {

        val allQuantities by dbViewModel.allQuantities.observeAsState(initial = emptyList())

        if (allQuantities != null) {


            val nameList: MutableList<String> = mutableListOf("n.d.", "n.d.", "n.d.")

            val sortedQuantities = allQuantities.sortedByDescending { it.quantity }

            for (i in 0..min(2, sortedQuantities.size - 1)) runBlocking {
                nameList[i] =
                    mainApplication.alimentoRepository.getAlimentoById(sortedQuantities[i].id)
                        .firstOrNull()?.nome ?: ""
            }


            val quantityList = mutableListOf(0.0, 0.0, 0.0)

            quantityList[0] = sortedQuantities.getOrNull(0)?.quantity?.toDouble() ?: 0.0
            quantityList[1] = sortedQuantities.getOrNull(1)?.quantity?.toDouble() ?: 0.0
            quantityList[2] = sortedQuantities.getOrNull(2)?.quantity?.toDouble() ?: 0.0


            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {

                BulletSpanRanking(color = Color(0xFFE1E289), label = nameList[0])
                BulletSpanRanking(color = Color(0xFF59C3C3), label = nameList[1])
                BulletSpanRanking(color = Color(0xFFDB504A), label = nameList[2])


                Surface(
                    modifier = Modifier
                        .height(325.dp)
                        .width(200.dp)
                        .background(Color.White)
                        .padding(8.dp),
                ) {

                    BarChart(
                        barChartData = BarChartData(
                            bars = listOf(
                                BarChartData.Bar(
                                    label = "",
                                    value = quantityList[2].toFloat(),
                                    color = Color(0xFFDB504A),
                                ), BarChartData.Bar(
                                    label = "",
                                    value = quantityList[1].toFloat(),
                                    color = Color(0xFF59C3C3)
                                ), BarChartData.Bar(
                                    label = "",
                                    value = quantityList[0].toFloat(),
                                    color = Color(0xFFE1E289)
                                )
                            )
                        ),
                        // Optional properties.
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(start = 30.dp, top = 8.dp, bottom = 8.dp),
                        animation = TweenSpec(500),
                        barDrawer = SimpleBarDrawer(),
                        xAxisDrawer = SimpleXAxisDrawer(
                            axisLineColor = Color.Transparent,
                        ),
                        yAxisDrawer = SimpleYAxisDrawer(
                            axisLineColor = Color.Transparent,
                        ),
                        labelDrawer = SimpleValueDrawer(
                            labelTextColor = Color.Transparent,
                        )
                    )


                }

            }


        }
    }

    class Macros(
        var proteine: Float = 0f,
        var carboidrati: Float = 0f,
        var grassi: Float = 0f,
        var fibre: Float = 0f,
    ) {
        fun add(macros: Macros) {
            proteine += macros.proteine
            carboidrati += macros.carboidrati
            grassi += macros.grassi
            fibre += macros.fibre
        }
    }

    @Composable
    private fun DisplayMacros() {

        val allPastoToCibo by dbViewModel.allPastoToCibo.observeAsState(initial = emptyList())

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


        val filteredPastoToCibo = allPastoToCibo.filter {
            it.date >= date
        }.sortedBy { it.date }


        val listMacro = LinkedHashMap<Int, Macros>()

        filteredPastoToCibo.forEach {
            val alimento by dbViewModel.loadAlimentoById(it.idAlimento)
                .observeAsState(initial = null)


            //alimento can be null ide error
            if (alimento != null) {

                //take the week calories
                if (listMacro.containsKey(it.date.day)) {
                    listMacro[it.date.day]?.add(
                        Macros(
                            it.quantity * alimento!!.proteine!!,
                            it.quantity * alimento!!.carboidrati!!,
                            it.quantity * alimento!!.grassi!!,
                            it.quantity * alimento!!.fibre!!
                        )
                    )
                } else listMacro[it.date.day] = Macros(
                    it.quantity * alimento!!.proteine!!,
                    it.quantity * alimento!!.carboidrati!!,
                    it.quantity * alimento!!.grassi!!,
                    it.quantity * alimento!!.fibre!!
                )
            }
        }

        val proteins = mutableListOf<Double>()
        val carbohydrates = mutableListOf<Double>()
        val fats = mutableListOf<Double>()
        val fibers = mutableListOf<Double>()




        listMacro.forEach {
            proteins.add(it.value.proteine.toDouble())
            carbohydrates.add(it.value.carboidrati.toDouble())
            fats.add(it.value.grassi.toDouble())
            fibers.add(it.value.fibre.toDouble())
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {

            if (filteredPastoToCibo != null && listMacro.isNotEmpty()) {
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 12.dp), data = listOf(
                        Line(
                            label = stringResource(id = R.string.protein),
                            values = proteins,
                            color = SolidColor(Color(0xFFDB504A)),
                            //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                            firstGradientFillColor = Color(0xFFDB504A).copy(alpha = .7f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                            gradientAnimationDelay = 500,
                            drawStyle = DrawStyle.Stroke(width = 2.dp)
                        ), Line(
                            label = stringResource(id = R.string.carboidrati),
                            values = carbohydrates,
                            color = SolidColor(Color(0xFFE1E289)),
                            //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                            firstGradientFillColor = Color(0xFFE1E289).copy(alpha = .7f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                            gradientAnimationDelay = 500,
                            drawStyle = DrawStyle.Stroke(width = 2.dp)
                        ), Line(
                            label = stringResource(id = R.string.grassi),
                            values = fats,
                            color = SolidColor(Color(0xFF59C3C3)),
                            //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                            firstGradientFillColor = Color(0xFF59C3C3).copy(alpha = .7f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                            gradientAnimationDelay = 500,
                            drawStyle = DrawStyle.Stroke(width = 2.dp)
                        ), Line(
                            label = stringResource(id = R.string.fibre),
                            values = fibers,
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
                    }), dividerProperties = DividerProperties(
                        enabled = true, xAxisProperties = LineProperties(
                            enabled = false
                        ), yAxisProperties = LineProperties(
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
    private fun DailyReport() {
        val allPastoToCibo by dbViewModel.allPastoToCibo.observeAsState(initial = emptyList())

        // Ottenere la data e l'ora corrente
        val today = Date()

        // Impostare l'ora a mezzanotte
        today.hours = 0
        today.minutes = 0
        today.seconds = 0

        val filterededPastoToCibo = allPastoToCibo.filter {
            it.date >= today
        }

        val dailyFoods: MutableList<Pair<Alimento, Float>> = mutableListOf()

        if (filterededPastoToCibo != null) {
            filterededPastoToCibo.forEach {
                val alimento by dbViewModel.loadAlimentoById(it.idAlimento)
                    .observeAsState(initial = null)

                if (alimento != null) {
                    dailyFoods.add(Pair(alimento!!, it.quantity))
                    Log.i("FILTERED", "LOOP")
                }


            }

            dailyFoods.forEach {
                proteine += it.first.proteine!!.times(it.second)
                carboidrati += it.first.carboidrati!!.times(it.second)
                grassi += it.first.grassi!!.times(it.second)
                fibre += it.first.fibre!!.times(it.second)
                calorie += it.first.calorie!!.times(it.second)

                Log.i("DAILY", "LOOP")
            }

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.dailyRemember),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .height(165.dp)
                        .width(165.dp),
                    contentAlignment = Alignment.Center
                ) {
                    //piechart con la somma dei valori nutrizionali
                    PieChart(
                        pieChartData = PieChartData(
                            listOf(
                                PieChartData.Slice(
                                    carboidrati, Color(0xFFE1E289)
                                ),
                                PieChartData.Slice(proteine, Color(0xFFDB504A)),
                                PieChartData.Slice(grassi, Color(0xFF59C3C3)),
                                PieChartData.Slice(fibre, Color(0xFF04724D)),
                            )
                        ),
                        modifier = Modifier.fillMaxSize(),
                        animation = simpleChartAnimation(),
                        sliceDrawer = SimpleSliceDrawer(sliceThickness = 10F)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Text(
                            text = calorie.toString(), fontSize = 18.sp
                        )

                        Text(
                            text = stringResource(id = R.string.kcal), fontSize = 18.sp
                        )

                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {

                                BulletSpan1(
                                    color = Color(0xFF59C3C3),
                                    label = stringResource(id = R.string.grassi),
                                    value = grassi.toInt()
                                )


                                BulletSpan1(
                                    color = Color(0xFF04724D),
                                    label = stringResource(id = R.string.fibre),
                                    value = fibre.toInt()
                                )

                                BulletSpan1(
                                    color = Color(0xFFE1E289),
                                    label = stringResource(id = R.string.carboidrati),
                                    value = carboidrati.toInt()
                                )

                                BulletSpan1(
                                    color = Color(0xFFDB504A),
                                    label = stringResource(id = R.string.protein),
                                    value = proteine.toInt()
                                )

                            }
                        }

                    }
                }

            }

        }


    }


    @Composable
    fun BulletSpan1(color: Color, label: String, value: Int) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
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
                text = "$label: ${value}g"
            )
        }
    }

    @Composable
    fun BulletSpanRanking(color: Color, label: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
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
                text = "${label.substring(0, min(15, label.length))}...",
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnrememberedMutableState")
    @Composable
    private fun WeightChart() {


        val user by mainApplication.userData!!.userData.observeAsState(initial = null)

        if (user != null) {
            val pairList = user!!.peso

            val weights = List(minOf(pairList!!.size, 7)) { 0.0 }.toMutableList()
            val currentDate = Date()
            val dates =
                List(minOf(pairList.size, 7)) { currentDate.toString() }.toMutableList()

            val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())

            pairList.subList(maxOf(0, pairList.size - 7), pairList.size)
                .forEachIndexed { index, pair ->
                    weights[index] = pair.second
                    dates[index] = formatter.format(pair.first)
                }

            val minWeight = weights.minOrNull() ?: 0.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp), data = listOf(
                        Line(
                            label = stringResource(id = R.string.ourPeso),
                            values = weights,
                            color = SolidColor(MaterialTheme.colorScheme.primary),
                            firstGradientFillColor = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                            gradientAnimationDelay = 500,
                            drawStyle = DrawStyle.Stroke(width = 2.dp)
                        )
                    ), animationMode = AnimationMode.Together(delayBuilder = {
                        it * 500L
                    }), dotsProperties = DotProperties(
                        enabled = false,
                        radius = 10f,
                        color = SolidColor(Color(0xFF04724D)),
                        strokeWidth = 3f,
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
                    minValue = minWeight,

                    labelProperties = LabelProperties(
                        enabled = true,
                        textStyle = MaterialTheme.typography.labelSmall,
                        labels = dates
                    )
                )

                FloatingActionButton(
                    onClick = { showUpdateWeightDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(45.dp),

                        backgroundColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.secondary


                ) {
                    Icon(imageVector = Icons.Default.Create, contentDescription = "")
                }

            }
        }


    }


}