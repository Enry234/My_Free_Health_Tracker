package com.example.myfreehealthtracker.localdatabase.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfreehealthtracker.localdatabase.Entities.UserData

class UserDataViewModel : ViewModel() {
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    fun setUserData(value: UserData?) {
        _userData.value = value
    }
}