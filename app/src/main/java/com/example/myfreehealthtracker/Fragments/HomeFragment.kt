package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.util.Date

class HomeFragment : Fragment(R.layout.fragment_home) {


    lateinit var mainApplication: MainApplication

    var user by mutableStateOf<UserData?>(null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        mainApplication = requireActivity().application as MainApplication
        composeView.setContent {
            //WeightChart()
        }
    }


    @Composable
    private fun WeightChart() {

        val userList by mainApplication.userDao.getAllUser().asLiveData()
            .observeAsState(initial = emptyList())
        //val user = mainApplication.userData!!


        if (userList == null) return

        val user = userList?.get(0)

        val pairList = user?.peso
        val weights = Array<Double>(pairList?.size!!) { 0.0 }
        val currentDate = Date()
        val dates = Array<Date>(pairList.size) { currentDate }

//        var entries = Array<Pair<Date, Double>>(pairList?.size!!) { Pair(currentDate, 0.0) }

        pairList.forEachIndexed { index, pair ->
            weights[index] = pair.second
            dates[index] = pair.first

            //entries[index] = pair
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