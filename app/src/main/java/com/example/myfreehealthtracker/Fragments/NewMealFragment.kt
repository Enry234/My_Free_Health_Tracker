package com.example.myfreehealthtracker.Fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myfreehealthtracker.R

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {

    private var foodList = mutableListOf<String>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmMealBtn: Button = view.findViewById(R.id.fragment_new_meal_confirm)

        confirmMealBtn.setOnClickListener {
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
    }
}