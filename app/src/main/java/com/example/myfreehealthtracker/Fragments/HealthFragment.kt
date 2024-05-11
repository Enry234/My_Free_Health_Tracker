package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.MainActivity
import com.example.myfreehealthtracker.Models.ExpandableFloatingActionButton
import com.example.myfreehealthtracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HealthFragment : Fragment(R.layout.fragment_health) {

    private lateinit var expandableFloatingActionButton: ExpandableFloatingActionButton
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            Toast.makeText(view.context, "ACTION1", Toast.LENGTH_SHORT).show()
        }
        expandableFloatingActionButton.setChildActionListener(secondChild) {
            Toast.makeText(view.context, "ACTION2", Toast.LENGTH_SHORT).show()
        }


    }
}
