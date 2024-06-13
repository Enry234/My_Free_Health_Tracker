package com.example.myfreehealthtracker.localdatabase.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfreehealthtracker.localdatabase.Daos.PastoToCiboDao
import com.example.myfreehealthtracker.localdatabase.Entities.Alimento
import com.example.myfreehealthtracker.localdatabase.Entities.Attivita
import com.example.myfreehealthtracker.localdatabase.Entities.Pasto
import com.example.myfreehealthtracker.localdatabase.Entities.PastoToCibo
import com.example.myfreehealthtracker.localdatabase.Entities.Sport
import com.example.myfreehealthtracker.localdatabase.Repositories.AlimentoRepository
import com.example.myfreehealthtracker.localdatabase.Repositories.AttivitaRepository
import com.example.myfreehealthtracker.localdatabase.Repositories.PastoRepository
import com.example.myfreehealthtracker.localdatabase.Repositories.PastoToCiboRepository
import com.example.myfreehealthtracker.localdatabase.Repositories.SportRepository
import kotlinx.coroutines.launch

class InternalDBViewModel(
    private val alimentoRepository: AlimentoRepository,
    private val pastoRepository: PastoRepository,
    private val pastoToCiboRepository: PastoToCiboRepository,
    private val sportRepository: SportRepository,
    private val attivitaRepository: AttivitaRepository


) : ViewModel() {




    //Alimento
    val allAlimento: LiveData<List<Alimento>> =
        alimentoRepository.allAlimento.asLiveData(viewModelScope.coroutineContext)

    fun insertAlimento(alimento: Alimento) = viewModelScope.launch {
        alimentoRepository.insertAlimento(alimento)
    }

    fun loadAlimentoById(id: String): LiveData<Alimento> {
        return alimentoRepository.getAlimentoById(id).asLiveData(viewModelScope.coroutineContext)
    }

    //Pasto
    val allPasto: LiveData<List<Pasto>> =
        pastoRepository.allPasto.asLiveData(viewModelScope.coroutineContext)

    fun insertPasto(pasto: Pasto) = viewModelScope.launch {
        pastoRepository.insertPasto(pasto)
    }

    fun loadAlimentiByPasto(pasto: Pasto): LiveData<List<Alimento>> {
        return pastoToCiboRepository.getAlimentiByPasto(pasto)
            .asLiveData(viewModelScope.coroutineContext)

    }


    //PastoToCibo
    fun insertPastoToCibo(pastoToCibo: PastoToCibo) = viewModelScope.launch {
        pastoToCiboRepository.insertPastoToCibo(pastoToCibo)
    }

    val allPastoToCibo: LiveData<List<PastoToCibo>> =
        pastoToCiboRepository.allPastoToCibo.asLiveData(viewModelScope.coroutineContext)

    fun loadQuantitaByPasto(pasto: Pasto, alimento: Alimento): LiveData<Float> {
        return pastoToCiboRepository.getQuantitaByPasto(pasto, alimento)
            .asLiveData(viewModelScope.coroutineContext)

    }

    val allQuantities: LiveData<List<PastoToCiboDao.AlimentoQuantity>> =
        pastoToCiboRepository.getAllQuantities().asLiveData(viewModelScope.coroutineContext)


    //sport
    val allSport: LiveData<List<Sport>> =
        sportRepository.allSports.asLiveData(viewModelScope.coroutineContext)

    fun insertSport(sport: Sport) = viewModelScope.launch {
        sportRepository.insertSport(sport)
    }

    fun loadSportById(id: String): LiveData<Sport> {
        return sportRepository.getSportById(id).asLiveData(viewModelScope.coroutineContext)
    }


    //attivita
    val allAttivita: LiveData<List<Attivita>> =
        attivitaRepository.allAttivita.asLiveData(viewModelScope.coroutineContext)

    fun insertAttivita(attivita: Attivita) = viewModelScope.launch {
        attivitaRepository.insertAttivita(attivita)
    }

}

class InternalViewModelFactory(
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
