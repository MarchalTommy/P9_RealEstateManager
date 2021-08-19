package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.repository.HouseRepository
import kotlinx.coroutines.Dispatchers
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

    fun getPictures(houseId: Int): LiveData<List<Picture>> {
        return repository.getPictures(houseId).asLiveData()
    }

    fun searchHouse(
        priceMax: Int = 999999999,
        priceMin: Int = 0,
        sizeMax: Int = 999999999,
        sizeMin: Int = 0,
        roomMax: Int = 1000,
        roomMin: Int = 1,
        bedroomMax: Int = 1000,
        bedroomMin: Int = 1,
        bathroomMax: Int = 1000,
        bathroomMin: Int = 1,
        type: String = "Villa"
    ): LiveData<List<HouseAndAddress>> {
        return repository.searchHouse(
            priceMax,
            priceMin,
            sizeMax,
            sizeMin,
            roomMax,
            roomMin,
            bedroomMax,
            bedroomMin,
            bathroomMax,
            bathroomMin,
            type
        ).asLiveData()
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

    // region UPDATES
    fun updateHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateHouse(house)
    }

    fun updateAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateAddress(address)
    }

    fun removePicture(picture: Picture) = viewModelScope.launch(Dispatchers.IO) {
        repository.removePicture(picture)
    }
    // endregion UPDATES

    fun getStaticMap(address: String, api: String): String {
        return repository.getStaticMap(address, api)
    }
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