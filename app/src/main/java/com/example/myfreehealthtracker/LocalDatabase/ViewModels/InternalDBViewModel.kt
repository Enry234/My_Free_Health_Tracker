package com.example.myfreehealthtracker.LocalDatabase.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.Attivita
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import com.example.myfreehealthtracker.LocalDatabase.Entities.Sport
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AlimentoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AttivitaRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.PastoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.PastoToCiboRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.SportRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.UserRepository
import kotlinx.coroutines.launch

class InternalDBViewModel(
    private val userRepository: UserRepository,
    private val alimentoRepository: AlimentoRepository,
    private val pastoRepository: PastoRepository,
    private val pastoToCiboRepository: PastoToCiboRepository,
    private val sportRepository: SportRepository,
    private val attivitaRepository: AttivitaRepository


) : ViewModel() {


    //User
    val allUser: LiveData<UserData> =
        userRepository.allUser.asLiveData(viewModelScope.coroutineContext)

    fun insertUser(user: UserData) = viewModelScope.launch {
        userRepository.insertUser(user)
    }


    //Alimento
    val allAlimento: LiveData<List<Alimento>> =
        alimentoRepository.allAlimento.asLiveData(viewModelScope.coroutineContext)

    fun insertAlimento(alimento: Alimento) = viewModelScope.launch {
        alimentoRepository.insertAlimento(alimento)
    }

    //Pasto
    val allPasto: LiveData<List<Pasto>> =
        pastoRepository.allPasto.asLiveData(viewModelScope.coroutineContext)

    fun insertPasto(pasto: Pasto) = viewModelScope.launch {
        pastoRepository.insertPasto(pasto)
    }

    //sport
    val allSport: LiveData<List<Sport>> =
        sportRepository.allSports.asLiveData(viewModelScope.coroutineContext)

    fun insertSport(sport: Sport) = viewModelScope.launch {
        sportRepository.insertSport(sport)
    }

    //attivita
    val allAttivita: LiveData<List<Attivita>> =
        attivitaRepository.allAttivita.asLiveData(viewModelScope.coroutineContext)

    fun insertAttivita(attivita: Attivita) = viewModelScope.launch {
        attivitaRepository.insertAttivita(attivita)
    }

}

class InternalViewModelFactory(
    private val userRepository: UserRepository,
    private val alimentoRepository: AlimentoRepository,
    private val pastoRepository: PastoRepository,
    private val pastoToCiboRepository: PastoToCiboRepository,
    private val sportRepository: SportRepository,
    private val attivitaRepository: AttivitaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternalDBViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InternalDBViewModel(
                userRepository,
                alimentoRepository,
                pastoRepository,
                pastoToCiboRepository,
                sportRepository,
                attivitaRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
