@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.ExpandableFloatingActionButton
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale

class HealthFragment : Fragment(R.layout.fragment_health) {

    private lateinit var expandableFloatingActionButton: ExpandableFloatingActionButton
    private lateinit var mainApplication: MainApplication


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainApplication = requireActivity().application as MainApplication

        val view = inflater.inflate(R.layout.fragment_health, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        val expander: FloatingActionButton = view.findViewById(
            R.id.component_expandable_floating_action_button_expander
        )
        val firstChild: FloatingActionButton = view.findViewById(
            R.id.component_expandable_floating_action_button_first_action_child
        )
        val secondChild: FloatingActionButton = view.findViewById(
            R.id.component_expandable_floating_action_button_second_action_child
        )

        expandableFloatingActionButton = ExpandableFloatingActionButton(
            view.findViewById(R.id.fragment_first_root),
            expander,
            firstChild,
            secondChild
        )

        expandableFloatingActionButton.setChildActionListener(firstChild) {
            // NEW MEAL
            view.findNavController().navigate(R.id.addMealFragment)
            //Toast.makeText(view.context, "ACTION1", Toast.LENGTH_SHORT).show()
        }
        expandableFloatingActionButton.setChildActionListener(secondChild) {

            view.findNavController().navigate(R.id.action_healthFragment_to_newFoodFragment)

//            val dialogAddFood = Dialog(view.context)
//            dialogAddFood.setContentView(R.layout.dialog_new_food)
//            dialogAddFood.show()
//
//            val composeViewContainer = dialogAddFood.findViewById<ComposeView>(R.id.compose_view)
//            composeViewContainer.apply {
//                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            }


            Toast.makeText(view.context, "ACTION2", Toast.LENGTH_SHORT).show()
        }
        val content = arguments?.getString("content")
        view.findViewById<TextView>(R.id.fragment_health_tv).apply {
            if (content != "") text = content
        }

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ShowMeal()
            }
        }
        return view
    }

    @Composable
    private fun ShowMeal() {
        val allPasto2 by mainApplication.pastoRepo.allPasto.observeAsState(initial = emptyList())
        val allPasto = allPasto2.sortedByDescending { it.date }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(

            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(allPasto) {
                        PastoItem(it)
                    }
                }
            }
        }
    }

    @Composable
    private fun PastoItem(pasto: Pasto) {

        val alimentiPasto: List<Alimento>? by mainApplication.pastoToCiboRepo.getAlimentiByPasto(
            pasto
        ).asLiveData().observeAsState()

        var calorie: Int = 0
        var carboidrati: Int = 0
        var proteine: Int = 0
        var grassi: Int = 0
        var fibre: Int = 0


        var isExpanded by rememberSaveable { mutableStateOf(false) }

        alimentiPasto?.forEach {


            var qta = 1f

            runBlocking {
                qta = mainApplication.pastoToCiboRepo.getQuantitaByPasto(pasto, it)
            }


            calorie += ((it.calorie!!) * qta).toInt()
            carboidrati += ((it.carboidrati!!) * qta).toInt()
            proteine += ((it.proteine!!) * qta).toInt()
            grassi += ((it.grassi!!) * qta).toInt()
            fibre += ((it.fibre!!) * qta).toInt()
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

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())


                    Text(
                        text = dateFormat.format(pasto.date),
                        modifier = Modifier.weight(3f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pasto.typePasto.toString(),
                        modifier = Modifier.weight(1.2f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold

                    )

                    IconButton(onClick = {
                        isExpanded = !isExpanded
                    }) {
                        val imageVector = if (isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        }
                        Icon(imageVector, contentDescription = "ToggleExpand")
                    }

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
                                        carboidrati.toFloat(),
                                        Color(0xFFE1E289)
                                    ),
                                    PieChartData.Slice(proteine.toFloat(), Color(0xFFDB504A)),
                                    PieChartData.Slice(grassi.toFloat(), Color(0xFF59C3C3)),
                                    PieChartData.Slice(fibre.toFloat(), Color(0xFF04724D)),
                                )
                            ),
                            modifier = Modifier.fillMaxSize(),
                            animation = simpleChartAnimation(),
                            sliceDrawer = SimpleSliceDrawer(sliceThickness = 15F)
                        )
                        Column(

                        ) {

                            Text(
                                text = calorie.toString()
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
                                            value = grassi
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFF04724D),
                                            label = "Fibre",
                                            value = fibre
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
                                            value = carboidrati
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BulletSpan(
                                            color = Color(0xFFDB504A),
                                            label = "Proteine",
                                            value = proteine
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(500)) + expandVertically(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500)) + shrinkVertically(animationSpec = tween(500))
        ) {
            Column {
                alimentiPasto?.forEach {


                    var qta = 1f

                    runBlocking {
                        qta = mainApplication.pastoToCiboRepo.getQuantitaByPasto(pasto, it)
                    }


                    DisplayAlimento(it, qta)
                }
            }
        }

    }

    private @Composable
    fun DisplayAlimento(alimento: Alimento, qta: Float) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = alimento.nome!!, fontWeight = FontWeight.Bold)
                    Text(text = "Calorie: ${alimento.calorie?.times(qta)?.toInt()} kcal")

                    val amount: String

                    if (alimento.unit.equals("100g")) {
                        amount = "${(qta * 100).toInt()}g"
                    } else {
                        amount = "${qta} unità"
                    }

                    Text(text = "Quantità: $amount")
                }
            }

            Box() {
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

}


//val dummyList = listOf<Assunzione>(
//    Assunzione("1", Date("13/10/2024"), 10, 2, "test1"),
//    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
//    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
//    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
//    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
//    Assunzione("1", Date("13/10/2024"), 8, 2, "test2")
//)

//@Preview
//@Composable
//fun HeadBar() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp)
//    ) {
//        LazyRow {
//            items(listOf("10", "11", "12", "13", "14", "15", "16", "17", "18", "19"))
//            {
//                ItemRow(it)
//            }
//
//        }
//    }
//}

//@Composable
//fun ItemRow(string: String) {
//    Box(
//        modifier = Modifier
//            .padding(5.dp)
//            .shadow(2.dp)
//            .clip(shape = CircleShape)
//            .background(color = androidx.compose.ui.graphics.Color.Red)
//    ) {
//        Text(text = string, modifier = Modifier.padding(10.dp))
//    }
//
//}
//@Preview
//@Composable
//fun ShowHealth() {
//    Surface(modifier = Modifier.fillMaxSize()) {}
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .fillMaxWidth(),
//        verticalArrangement = Arrangement.Bottom,
//        horizontalAlignment = Alignment.CenterHorizontally,
//
//    ) {
//        Row {
//            HeadBar()
//            Text(text = "Storico pasti e medicine", modifier = Modifier.padding(20.dp))
//
//        }
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .fillMaxHeight()
//        ) {
//            items(dummyList) { assunzione ->
//                MessageRow(assunzione)
//            }
//        }
//    }
//
//}
//
//@Composable
//fun MessageRow(assunzione: Assunzione) {
//    Column {
//
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//
//                .shadow(elevation = 1.dp, shape = RoundedCornerShape(10.dp))
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .fillMaxSize()
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceAround
//                ) {
//                    TextField(
//
//                        modifier = Modifier
//                            .width(150.dp)
//                            .padding(10.dp),
//                        value = assunzione.date.toString(),
//                        onValueChange = {},
//                        readOnly = true,
//                        singleLine = true,
//                        label = {
//                            Text(
//                                text = "Data"
//                            )
//                        })
//                    TextField(
//                        modifier = Modifier
//                            .width(150.dp)
//                            .padding(10.dp),
//                        value = assunzione.idMedicina.toString(),
//                        onValueChange = {},
//                        readOnly = true,
//                        singleLine = true,
//                        label = {
//                            Text(
//                                text = "Medicina"
//                            )
//                        })
//                }
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    TextField(
//                        modifier = Modifier
//                            .width(150.dp)
//                            .padding(10.dp),
//                        value = assunzione.note,
//                        onValueChange = {},
//                        readOnly = true,
//                        singleLine = true,
//                        label = {
//                            Text(
//                                text = "Note"
//                            )
//                        })
//                    TextField(
//                        modifier = Modifier
//                            .width(150.dp)
//                            .padding(10.dp),
//                        value = assunzione.quantita.toString(),
//                        onValueChange = {},
//                        readOnly = true,
//                        singleLine = true,
//                        label = {
//                            Text(
//                                text = "Quantita'"
//                            )
//                        })
//                }
//
//            }
//        }
//        Spacer(modifier = Modifier.padding(16.dp))
//    }
//}
