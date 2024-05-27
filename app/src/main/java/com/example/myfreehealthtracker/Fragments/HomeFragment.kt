package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R

class HomeFragment : Fragment(R.layout.fragment_home) {


    lateinit var mainApplication: MainApplication

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}