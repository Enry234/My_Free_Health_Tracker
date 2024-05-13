package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myfreehealthtracker.LocalDatabase.InternalDBViewModel
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import com.example.myfreehealthtracker.LocalDatabase.InternalViewModelFactory
import com.example.myfreehealthtracker.LocalDatabase.Repository
import com.example.myfreehealthtracker.Models.UserData
import com.example.myfreehealthtracker.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val db by lazy {
        InternalDatabase.getDatabase(requireContext())
    }
    private val viewModel by viewModels<InternalDBViewModel>(
        factoryProducer = {
            InternalViewModelFactory(repository = Repository(db.userDao()))
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.insert(
            UserData(
                "nome", "cognome", "asc123", "11/11/2022", 'm', 180, "",
                ""
            )
        )
        var list = listOf<UserData>()
        viewModel.allUser.observe(viewLifecycleOwner) {
            list = it
        }

    }
}