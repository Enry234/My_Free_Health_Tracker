package com.example.myfreehealthtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.Fragments.HealthFragment
import com.example.myfreehealthtracker.Fragments.HomeFragment
import com.example.myfreehealthtracker.Fragments.SportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val homeFragment = HomeFragment()
        val sportFragment = SportFragment()
        val healthFragment = HealthFragment()

        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        makeCurrentFragment(homeFragment)

        bottom_nav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Home -> makeCurrentFragment(homeFragment)
                R.id.Health -> makeCurrentFragment(healthFragment)
                R.id.Sport -> makeCurrentFragment(sportFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
}

