package com.openclassrooms.realestatemanager.repository

import com.openclassrooms.realestatemanager.database.FirestoreHelper
import com.openclassrooms.realestatemanager.database.dao.HouseDao
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import kotlinx.coroutines.flow.Flow

class HouseRepository(private val houseDao: HouseDao) {

    // region GETTERS
    val allHouses: Flow<List<House>> = houseDao.getAllHouses()

    val allHousesWithAddress: Flow<List<HouseAndAddress>> = houseDao.getAllHousesAndAddresses()

    fun getHouseWithId(id: Int): Flow<House> {
        return houseDao.getHouseWithId(id)
    }

    fun getAddressFromHouse(houseId: Int): Flow<Address> {
        return houseDao.getAddressFromHouse(houseId)
    }

    fun getHouseWithAddress(houseId: Int): Flow<List<HouseAndAddress>> {
        return houseDao.getHouseWithAddress(houseId)
    }

    fun getAgent(agentId: Int): Flow<Agent> {
        return houseDao.getAgent(agentId)
    }
    // endregion GETTERS


    // region INSERTS
    suspend fun insertHouse(house: House) {
        houseDao.insertHouse(house)
    }

    fun insertAddress(address: Address) {
        insertAddress(address)
    }

    fun insertAgent(agent: Agent) {
        insertAgent(agent)
    }
    // endregion INSERTS


}