package com.example.myfreehealthtracker.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myfreehealthtracker.R

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {

    private var foodList = mutableStateListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodList.add("Testpizza")
        val confirmMealBtn: Button = view.findViewById(R.id.fragment_new_meal_confirm)

        confirmMealBtn.setOnClickListener {
            //TODO
            view.findNavController().navigate(
                R.id.action_confirm_new_meal,
                bundleOf("content" to "placeholder")
            )
        }

        val dialogAddFood = Dialog(view.context)
        dialogAddFood.setContentView(R.layout.fragment_new_meal_add_food_dialog)

        val addNewFoodBtn: Button = view.findViewById(R.id.fragment_new_meal_add_food)
        addNewFoodBtn.setOnClickListener {
            dialogAddFood.show()
        }

        dialogAddFood.findViewById<Button>(R.id.dialog_food_cancel_button).setOnClickListener {
            dialogAddFood.dismiss()
        }

        dialogAddFood.findViewById<Button>(R.id.dialog_food_existing_button).setOnClickListener {
            val dialogAddExistingFood = Dialog(view.context)
            dialogAddExistingFood.setContentView(R.layout.dialog_new_food_spinner)
            val spinner = dialogAddExistingFood.findViewById<Spinner>(R.id.dialog_existing_spinner)

            val existingFoodItems = listOf("pizzooooooooooooooooooooooooo", "trette", "calle", "carletto", "pizzo", "trette", "calle", "carletto", "pizzo", "trette", "calle", "carletto", "pizzo", "trette", "calle", "carletto")
            val foodAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, existingFoodItems)
            foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = foodAdapter

            dialogAddExistingFood.setOnCancelListener {
                dialogAddFood.show()
            }
            dialogAddFood.dismiss()
            dialogAddExistingFood.show()

            dialogAddExistingFood.findViewById<Button>(R.id.dialog_existing_spinner_cancel).setOnClickListener {
                dialogAddExistingFood.dismiss()
                dialogAddFood.show()
            }

            dialogAddExistingFood.findViewById<Button>(R.id.dialog_existing_spinner_confirm).setOnClickListener {
                foodList.add(spinner.selectedItem.toString())
                dialogAddExistingFood.dismiss()
            }
        }
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight()
                ) {
                    items(foodList) { food ->
                        Item(food)
                    }
                }
            }
        }
    }
}

@Composable
fun Item(string: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(color = Color.LightGray)
            .fillMaxHeight(0.2f)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = string)
            Text(text = "Quantita")
            Text(text = "altro testo")

        }

    }

}