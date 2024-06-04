package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
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
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment(R.layout.fragment_home) {


    lateinit var mainApplication: MainApplication

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
                            tonalElevation = 16.dp,
                            shadowElevation = 16.dp,
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                WeightChart()
                            }
                        }
                    }
                }
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

        pairList!!.subList(maxOf(0, pairList.size - 7), pairList.size).forEachIndexed { index, pair ->
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
                dividerProperties = DividerProperties (
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


//        val chartEntryModel = entryModelOf(*weights)
//

//
//        //val xValuesToDates = data.associateBy { it.to.toFloat() }
//        //val chartEntryModel = entryModelOf(xValuesToDates.keys.zip(data.values, ::entryOf))
//        //val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
//        val horizontalAxisValueFormatter =
//            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
//                formatter.format(dates[value.toInt()])
//            }
//
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//
//        ) {
//
//            ProvideChartStyle(
//                ChartStyle.Axis()
//            ){
//                Chart(
//                    modifier = Modifier.fillMaxWidth(),
//                    chart = lineChart(
//
//                    ),
//                    model = chartEntryModel,
//                    startAxis = rememberStartAxis(valueFormatter = DecimalFormatAxisValueFormatter(),
//                        itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 10, true) ),
//                    bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter),
//                    autoScaleUp = AutoScaleUp.Full,
//                    chartScrollState = rememberChartScrollState(),
//
//                    )
//    }


    }


}