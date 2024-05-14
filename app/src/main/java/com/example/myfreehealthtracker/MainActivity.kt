package com.example.myfreehealthtracker

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(R.layout.layout_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeMainActivityLayout()

    }

    private fun initializeMainActivityLayout() {
        val bottomNavBar = findViewById<BottomNavigationView>(
            R.id.app_bottom_navigation_bar
        )
        val toolbarTitle = findViewById<TextView>(R.id.activity_main_toolbar_title)
        val drawerLayout = findViewById<DrawerLayout>(R.id.app_drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.app_drawer_navigation_view)

        val fragmentContainer = findViewById<FragmentContainerView>(
            R.id.activity_main_fragment_container
        )

        findViewById<ImageView>(R.id.activity_main_toolbar_profile_picture)
            .setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        bottomNavBar.setOnItemSelectedListener {
            val navController = fragmentContainer.findNavController()
            when (it.itemId) {
                R.id.Home -> {
                    toolbarTitle.text = getString(R.string.Home)
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.Sport -> {
                    toolbarTitle.text = getString(R.string.Sport)
                    navController.navigate(R.id.sportFragment)
                    true
                }

                R.id.Health -> {
                    toolbarTitle.text = getString(R.string.Health)
                    navController.navigate(R.id.healthFragment)
                    true
                }

                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener {
            val navController = fragmentContainer.findNavController()
            when (it.itemId) {
                R.id.Logout -> {
                    Log.i("Logout", "Entrato condizioner")
                    navController.navigate(R.id.sportFragment)
                    true
                }
                else -> false
            }
        }

    }
}

