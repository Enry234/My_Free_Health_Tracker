package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData
import com.example.myfreehealthtracker.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {


    lateinit var mainApplication: MainApplication

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainApplication = requireActivity().application as MainApplication
        lifecycleScope.launch {
            mainApplication.userDao.getUser().collectLatest { user ->
                user.forEach {
                    println(it.id)
                }
            }
        }
        lifecycleScope.launch {
            mainApplication.userDao.insertUser(UserData("Test", "test", "98765443231"))
        }
    }
}