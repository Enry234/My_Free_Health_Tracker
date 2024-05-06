package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.R
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = view.findViewById<NavigationView>(R.id.navigation_view)

        val button: ImageButton = view.findViewById(R.id.btn_open_drawer)

// Aprire il NavigationView
        button.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

// Chiudere il NavigationView
        navView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            // Chiudi il NavigationView
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        val bottomNav = view.findViewById<NavigationView>(R.id.navigation_view)


//        bottomNav.setNavigationItemSelectedListener { menuItem ->
////            when (menuItem.itemId) {
////                R.id.Logout -> makeCurrentFragment(HomeFragment())
////            }
////            true
//        }


        // Inflate the layout for this fragment
        return view
    }

}