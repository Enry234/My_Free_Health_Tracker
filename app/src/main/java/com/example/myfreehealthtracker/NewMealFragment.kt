package com.example.myfreehealthtracker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class NewMealFragment : Fragment(R.layout.fragment_new_meal) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn: Button = view.findViewById(R.id.fragment_new_meal_confirm)

        btn.setOnClickListener {
            val content: EditText = view.findViewById(R.id.fragment_new_meal_content)
            view.findNavController().navigate(
                R.id.action_confirm_new_meal,
                bundleOf("content" to content.text.toString())
            )
        }
    }

}