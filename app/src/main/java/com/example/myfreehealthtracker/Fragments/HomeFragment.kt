package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var mainApplication: MainApplication
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        mainApplication = requireActivity().application as MainApplication
        composeView.setContent {
            Box(modifier = Modifier.height(300.dp))
            {
                if (mainApplication.userData != null && mainApplication.userData!!.peso!!.isNotEmpty()) {


                    val refreshDataset = remember {
                        mutableIntStateOf(0)
                    }
                    val modelProducer = remember { ChartEntryModelProducer() }
                    val datasetForModel = remember { mutableStateListOf(listOf<FloatEntry>()) }
                    val datasetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
                    val scrollState = rememberChartScrollState()





                    LaunchedEffect(key1 = refreshDataset.intValue) {

                        datasetLineSpec.clear()
                        var xPos = 0f
                        val dataPoints = arrayListOf<FloatEntry>()
                        datasetLineSpec.add(
                            LineChart.LineSpec(
                                lineColor = R.color.purple_200,
                                lineBackgroundShader = DynamicShaders.fromBrush(
                                    brush = Brush.Companion.verticalGradient(
                                        listOf(
                                            Red.copy(0.5f), Red.copy(0.8f)
                                        )
                                    )
                                )
                            )
                        )
                        mainApplication.userData!!.peso!!.sortBy { it.first }
                        mainApplication.userData!!.peso!!.forEach {
                            val y = it.second.toFloat()
                            dataPoints.add(FloatEntry(xPos, y))
                            xPos += 1f
                        }

                        datasetForModel.add(dataPoints)
                        modelProducer.setEntries(datasetForModel)
                    }
                    if (datasetForModel.isNotEmpty()) {
                        ProvideChartStyle {
                            Chart(
                                chart = lineChart(
                                    lines = datasetLineSpec,
                                ),
                                startAxis = rememberStartAxis(
                                    title = "peso",
                                    tickLength = 0.dp,
                                    valueFormatter = { value, _ ->
                                        (value.toInt()).toString()
                                    },
                                    itemPlacer = AxisItemPlacer.Vertical.default(
                                        maxItemCount = 10
                                    )

                                ),
                                bottomAxis = rememberBottomAxis(
                                    title = "Date",
                                    tickLength = 0.dp,
                                    valueFormatter = { value, _ ->
                                        (value.toInt()).toString()
                                    }
                                ),
                                chartModelProducer = modelProducer,
                                chartScrollState = scrollState,
                                isZoomEnabled = true
                            )
                        }
                    }

                }
            }


        }
    }
}