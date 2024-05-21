@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myfreehealthtracker.ExpandableFloatingActionButton
import com.example.myfreehealthtracker.Models.Assunzione
import com.example.myfreehealthtracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date

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
            view.findNavController().navigate(R.id.action_health_fragment_new_meal)
            //Toast.makeText(view.context, "ACTION1", Toast.LENGTH_SHORT).show()
        }
        expandableFloatingActionButton.setChildActionListener(secondChild) {
            Toast.makeText(view.context, "ACTION2", Toast.LENGTH_SHORT).show()
        }
        val content = arguments?.getString("content")
        view.findViewById<TextView>(R.id.fragment_health_tv).apply {
            if (content != "") text = content
        }

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ShowHealth()

            }
        }
        return view
    }

}

val dummyList = listOf<Assunzione>(
    Assunzione("1", Date("13/10/2024"), 10, 2, "test1"),
    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
    Assunzione("1", Date("13/10/2024"), 8, 2, "test2"),
    Assunzione("1", Date("13/10/2024"), 8, 2, "test2")
)

@Preview
@Composable
fun HeadBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        LazyRow {
            items(listOf("10", "11", "12", "13", "14", "15", "16", "17", "18", "19"))
            {
                ItemRow(it)
            }

        }
    }
}

@Composable
fun ItemRow(string: String) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .shadow(2.dp)
            .clip(shape = CircleShape)
            .background(color = androidx.compose.ui.graphics.Color.Red)
    ) {
        Text(text = string, modifier = Modifier.padding(10.dp))
    }

}
@Preview
@Composable
fun ShowHealth() {
    Surface(modifier = Modifier.fillMaxSize()) {}
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Row {
            HeadBar()
            Text(text = "Storico pasti e medicine", modifier = Modifier.padding(20.dp))

        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
        ) {
            items(dummyList) { assunzione ->
                MessageRow(assunzione)
            }
        }
    }

}

@Composable
fun MessageRow(assunzione: Assunzione) {
    Column {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

                .shadow(elevation = 1.dp, shape = RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextField(

                        modifier = Modifier
                            .width(150.dp)
                            .padding(10.dp),
                        value = assunzione.date.toString(),
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text(
                                text = "Data"
                            )
                        })
                    TextField(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(10.dp),
                        value = assunzione.idMedicina.toString(),
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text(
                                text = "Medicina"
                            )
                        })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(10.dp),
                        value = assunzione.note,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text(
                                text = "Note"
                            )
                        })
                    TextField(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(10.dp),
                        value = assunzione.quantita.toString(),
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text(
                                text = "Quantita'"
                            )
                        })
                }

            }
        }
        Spacer(modifier = Modifier.padding(16.dp))
    }
}
