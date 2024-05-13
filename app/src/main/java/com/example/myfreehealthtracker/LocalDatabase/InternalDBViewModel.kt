package com.example.myfreehealthtracker.LocalDatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfreehealthtracker.Models.UserData
import kotlinx.coroutines.launch

class InternalDBViewModel(private val repository: Repository) : ViewModel() {


    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes)
    //   and only update the the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allUser: LiveData<List<UserData>> = repository.user.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
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
