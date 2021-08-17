package com.openclassrooms.realestatemanager.database.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseDao {

    @Query("DELETE FROM house")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouse(house: House)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: Picture)

    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseWithId(houseId: Int): Flow<House>

    @Query("SELECT * FROM house WHERE addressId = :addressId")
    fun getHouseOfAddress(addressId: Int): Flow<List<House>>

    @Query("SELECT * FROM House WHERE agentId = :agentId")
    suspend fun getHousesOfAgent(agentId: Int): List<House>

    @Query("SELECT * FROM house WHERE type = :type")
    suspend fun getHouseOfType(type: String): List<House>

    @Query("SELECT * FROM agent WHERE id = :agentId")
    fun getAgent(agentId: Int): Flow<Agent>

    @Query("SELECT * FROM house")
    fun getAllHouses(): Flow<List<House>>

    @Query("SELECT * FROM address WHERE houseId = :houseId")
    fun getAddressFromHouse(houseId: Int): Flow<Address>

    @Transaction
    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseWithAddress(houseId: Int): Flow<List<HouseAndAddress>>

    @Transaction
    @Query("SELECT * FROM house")
    fun getAllHousesAndAddresses(): Flow<List<HouseAndAddress>>

    @Query("SELECT * FROM picture WHERE houseId = :houseId")
    fun getPicturesFromHouse(houseId: Int): Flow<List<Picture>>

    @Query("SELECT uri FROM picture WHERE houseId = :houseId")
    fun getPicUriFromHouse(houseId: Int): Flow<List<String>>

    @Query("SELECT * FROM house WHERE price < :priceMax AND price > :priceMin AND size < :sizeMax AND size > :sizeMin AND nbrRooms < :roomMax AND nbrRooms > :roomMin AND nbrBedrooms < :bedroomMax AND nbrBedrooms > :bedroomMin AND nbrBathrooms < :bathroomMax AND nbrBathrooms > :bathroomMin AND type = :type AND dateSell = :dateSold AND dateEntryOnMarket = :dateCreated AND stillAvailable = :available")
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
    ): Flow<List<House>>

    @Update
    fun updateHouse(house: House)

    @Update
    fun updateAddress(address: Address)

    @Delete
    fun removePicture(picture: Picture)
}