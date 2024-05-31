package com.example.myfreehealthtracker.LocalDatabase.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AlimentoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.UserRepository
import kotlinx.coroutines.launch

class InternalDBViewModel(private val userRepository: UserRepository, private val alimentoRepository: AlimentoRepository) : ViewModel() {


    val allUser: LiveData<List<UserData>> =
        userRepository.allUser.asLiveData(viewModelScope.coroutineContext)

    val allAlimento: LiveData<List<Alimento>> =
        alimentoRepository.allAlimento.asLiveData(viewModelScope.coroutineContext)


    fun insert(user: UserData) = viewModelScope.launch {
        userRepository.insert(user)
    }
}

class InternalViewModelFactory(private val userRepository: UserRepository, private val alimentoRepository: AlimentoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternalDBViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InternalDBViewModel(userRepository, alimentoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
