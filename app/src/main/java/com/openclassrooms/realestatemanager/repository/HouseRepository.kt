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
        type: String = "Villa",
        dateSold: String = "",
        dateCreated: String = "27/05/2020",
        available: Boolean = true
    ): Flow<List<House>> {
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
            type,
            dateSold,
            dateCreated,
            available
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