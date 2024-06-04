package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.util.Date

class HomeFragment : Fragment(R.layout.fragment_home) {


    lateinit var mainApplication: MainApplication

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApplication = requireActivity().application as MainApplication

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        mainApplication.userData!!.userData.observe(viewLifecycleOwner) {
            // Update the UI, in this case, a TextView.
            composeView.setContent {
                WeightChart()

            }
        }
    }


    @SuppressLint("UnrememberedMutableState")
    @Composable
    private fun WeightChart() {


        val pairList = mainApplication.userData!!.userData.value!!.peso
        val weights = Array<Double>(pairList?.size!!) { 0.0 }
        val currentDate = Date()
        val dates = Array<Date>(pairList.size) { currentDate }

        var entries = Array<Pair<Date, Double>>(pairList?.size!!) { Pair(currentDate, 0.0) }

        pairList.forEachIndexed { index, pair ->
            weights[index] = pair.second
            dates[index] = pair.first

            entries[index] = pair
        }


        val chartEntryModel = entryModelOf(*weights)

        Chart(
            chart = lineChart(),
            model = chartEntryModel,
            startAxis = rememberStartAxis(),
            //bottomAxis = rememberBottomAxis(),
        )


    }
}