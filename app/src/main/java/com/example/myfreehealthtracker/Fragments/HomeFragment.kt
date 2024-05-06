package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myfreehealthtracker.R

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val drawerLayout: DrawerLayout? = activity?.findViewById(R.id.home_drawer)

        // Verifica se il DrawerLayout è nullo
        if (drawerLayout == null) {
            Log.e("Fragment", "DrawerLayout is null")
            return view // Ritorna il layout del fragment senza impostare il click listener sul pulsante
        }

        // Trova il pulsante nell'layout del fragment
        val btnOpenDrawer: ImageButton = view.findViewById(R.id.btn_open_drawer)
        btnOpenDrawer.setOnClickListener {
            // Verifica se il Navigation Drawer è aperto o chiuso
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                // Se è aperto, chiudilo
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // Se è chiuso, aprilo
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        // Inflate the layout for this fragment
        return view
    }
}