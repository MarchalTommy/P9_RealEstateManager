package com.openclassrooms.realestatemanager.repository

import com.openclassrooms.realestatemanager.database.dao.HouseDao
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
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

    fun getPictures(houseId: Int): Flow<List<Picture>> {
        return houseDao.getPicturesFromHouse(houseId)
    }

    fun searchHouse(
        priceMax: Int,
        priceMin: Int,
        sizeMax: Int,
        sizeMin: Int,
        roomMax: Int,
        roomMin: Int,
        bedroomMax: Int,
        bedroomMin: Int,
        bathroomMax: Int,
        bathroomMin: Int,
        type: String
    ): Flow<List<HouseAndAddress>> {
        return houseDao.searchHouse(
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
        )
    }
    // endregion GETTERS


    // region INSERTS
    suspend fun insertHouse(house: House) {
        houseDao.insertHouse(house)
    }

    suspend fun insertAddress(address: Address) {
        houseDao.insertAddress(address)
    }

    suspend fun insertAgent(agent: Agent) {
        houseDao.insertAgent(agent)
    }

    suspend fun insertPicture(picture: Picture) {
        houseDao.insertPicture(picture)
    }
    // endregion INSERTS

    fun updateHouse(house: House) {
        houseDao.updateHouse(house)
    }

    fun updateAddress(address: Address) {
        houseDao.updateAddress(address)
    }

    fun removePicture(picture: Picture) {
        houseDao.removePicture(picture)
    }

}