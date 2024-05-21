package com.example.myfreehealthtracker.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myfreehealthtracker.R

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {

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
    }
}