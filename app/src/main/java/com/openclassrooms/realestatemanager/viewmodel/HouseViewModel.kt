package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.repository.HouseRepository
import kotlinx.coroutines.launch

class HouseViewModel(private val repository: HouseRepository) : ViewModel() {

    // region GETTERS

    val allHouses: LiveData<List<House>> = repository.allHouses.asLiveData()

    val allHousesWithAddress: LiveData<List<HouseAndAddress>> =
        repository.allHousesWithAddress.asLiveData()

    fun getHouseWithId(houseId: Int): LiveData<House> {
        return repository.getHouseWithId(houseId).asLiveData()
    }

    fun getAddressFromHouse(houseId: Int): LiveData<Address> {
        return repository.getAddressFromHouse(houseId).asLiveData()
    }

    fun getHouseWithAddress(houseId: Int): LiveData<List<HouseAndAddress>> {
        return repository.getHouseWithAddress(houseId).asLiveData()
    }

    fun getAgent(agentId: Int): LiveData<Agent> {
        return repository.getAgent(agentId).asLiveData()
    }
    // endregion GETTERS

    // region INSERTS

    fun insertHouse(house: House) = viewModelScope.launch {
        repository.insertHouse(house)
    }

    fun insertAddress(address: Address) = viewModelScope.launch {
        repository.insertAddress(address)
    }

    fun insertAgent(agent: Agent) = viewModelScope.launch {
        repository.insertAgent(agent)
    }

    fun insertPicture(picture: Picture) = viewModelScope.launch {
        repository.insertPicture(picture)
    }

    // endregion INSERTS

    // TODO : UPDATES FUN
    // region UPDATES
//    fun updateHouse(house: House) = viewModelScope.launch {
//        repository.updateHouse(house)
//    }
    // endregion UPDATES
}

class HouseViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HouseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}