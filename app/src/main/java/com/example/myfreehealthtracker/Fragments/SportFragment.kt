package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.LoginPage


class SportFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val a = LoginPage()
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                a.Login()
            }
        }
    }

}