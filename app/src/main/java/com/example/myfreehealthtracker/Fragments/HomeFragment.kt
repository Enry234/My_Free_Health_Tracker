package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseInOutCubic
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
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
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment(R.layout.fragment_home) {


    lateinit var mainApplication: MainApplication

    var proteine = 0f
    var carboidrati = 0f
    var grassi = 0f
    var fibre = 0f
    var calorie = 0f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApplication = requireActivity().application as MainApplication

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        mainApplication.userData!!.userData.observe(viewLifecycleOwner) {
            // Update the UI, in this case, a TextView.
            composeView.setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
                    ) {

                        Surface(
                            Modifier.fillMaxWidth(),
                            tonalElevation = 16.dp,
                            shadowElevation = 16.dp,
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                dailyReport()
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            tonalElevation = 16.dp,
                            shadowElevation = 16.dp,
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

    @Composable
    private fun dailyReport() {
        val allPastoToCibo by (mainApplication).pastoToCiboRepo.getAllPastoToCibo().observeAsState()

        // Ottenere la data e l'ora corrente
        val today = Date()

        // Impostare l'ora a mezzanotte
        today.hours = 0
        today.minutes = 0
        today.seconds = 0

        var filterededPastoToCibo = allPastoToCibo?.filter {
            it.date.compareTo(today) >= 0
        }

        var dailyFoods: MutableList<Pair<Alimento, Float>> = mutableListOf()

        if (filterededPastoToCibo != null) {
            filterededPastoToCibo.forEach {
                var alimento: Alimento
                runBlocking {
                    alimento = mainApplication.alimentoRepo.getAlimentoById(it.idAlimento)
                }

                dailyFoods.add(Pair(alimento, it.quantity))
                Log.i("FILTERED", "LOOP")

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    //piechart con la somma dei valori nutrizionali
                    PieChart(
                        pieChartData = PieChartData(
                            listOf(
                                PieChartData.Slice(
                                    carboidrati,
                                    Color(0xFFE1E289)
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
                            text = "Riepilogo Giornaliero",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = calorie.toString(),
                            fontSize = 18.sp
                        )

                        Text(
                            text = "kcal",
                            fontSize = 18.sp
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
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BulletSpan1(
                                        color = Color(0xFF59C3C3),
                                        label = "Grassi",
                                        value = grassi.toInt()
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BulletSpan1(
                                        color = Color(0xFF04724D),
                                        label = "Fibre",
                                        value = fibre.toInt()
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BulletSpan2(
                                        color = Color(0xFFE1E289),
                                        label = "Carboidrati",
                                        value = carboidrati.toInt()
                                    )
                                }
                                Row(

                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BulletSpan2(
                                        color = Color(0xFFDB504A),
                                        label = "Proteine",
                                        value = proteine.toInt()
                                    )
                                }
                            }
                        }
                    }
                }

            }

        }


    }


    @Composable
    fun BulletSpan1(color: Color, label: String, value: Int) {
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
    fun BulletSpan2(color: Color, label: String, value: Int) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label: ${value}g"
            )

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
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnrememberedMutableState")
    @Composable
    private fun WeightChart() {


        val pairList = mainApplication.userData!!.userData.value!!.peso
        val weights = List<Double>(minOf(pairList!!.size, 7)) { 0.0 }.toMutableList()
        val currentDate = Date()
        val dates = List<String>(minOf(pairList.size, 7)) { currentDate.toString() }.toMutableList()

        val dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM")
        val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())

        pairList!!.subList(maxOf(0, pairList.size - 7), pairList.size)
            .forEachIndexed { index, pair ->
                weights[index] = pair.second
                dates[index] = formatter.format(pair.first)
            }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                data = listOf(
                    Line(
                        label = "Il tuo peso",
                        values = weights,
                        color = SolidColor(Color(0xFF04724D)),
                        //color= Brush.radialGradient( 0.3f to Color.Green,1.0f to Color.Red),
                        firstGradientFillColor = Color(0xFF04724D).copy(alpha = .7f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                        gradientAnimationDelay = 500,
                        drawStyle = DrawStyle.Stroke(width = 2.dp)
                    )
                ),
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                minValue = 40.0,
                //maxValue = 100.0
                dotsProperties = DotProperties(
                    enabled = false,
                    radius = 10f,
                    color = SolidColor(Color(0xFF04724D)),
                    strokeWidth = 3f,
                    //strokeColor = Color.White,
                    strokeStyle = StrokeStyle.Normal,
                    animationEnabled = true,
                    animationSpec = tween(500)
                ),
                dividerProperties = DividerProperties(
                    enabled = true,
                    xAxisProperties = LineProperties(
                        enabled = false
                    ),
                    yAxisProperties = LineProperties(
                        enabled = false
                    )
                ),

                gridProperties = GridProperties(
                    enabled = false,
                ),

                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelSmall,
                    //verticalPadding = 16.dp,
                    labels = dates
                )
            )
        }


    }


}