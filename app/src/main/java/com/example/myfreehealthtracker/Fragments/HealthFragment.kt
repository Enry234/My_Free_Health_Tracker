@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myfreehealthtracker.ExpandableFloatingActionButton
import com.example.myfreehealthtracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HealthFragment : Fragment(R.layout.fragment_health) {

    private lateinit var expandableFloatingActionButton: ExpandableFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                //ShowHealth()
            }
        }
        return view
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
