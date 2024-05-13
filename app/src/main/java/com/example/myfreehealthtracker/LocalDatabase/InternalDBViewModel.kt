package com.example.myfreehealthtracker.LocalDatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfreehealthtracker.Models.UserData
import kotlinx.coroutines.launch

class InternalDBViewModel(private val repository: Repository) : ViewModel() {


    val allUser: LiveData<List<UserData>> =
        repository.user.asLiveData(viewModelScope.coroutineContext)


    fun insert(user: UserData) = viewModelScope.launch {
        repository.insert(user)
    }
}

class InternalViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternalDBViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InternalDBViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
