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

    //region INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouse(house: House)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: Picture)

    //endregion INSERT

    //region QUERY
    @Query("DELETE FROM house")
    suspend fun deleteAll()

    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseWithId(houseId: Int): Flow<House>

    @Query("SELECT * FROM address WHERE zip != 0")
    fun getAllAddresses(): Flow<List<Address>>

    @Query("SELECT * FROM House WHERE agentId = :agentId")
    suspend fun getHousesOfAgent(agentId: Int): List<House>

    @Query("SELECT * FROM house WHERE type = :type")
    suspend fun getHouseOfType(type: String): List<House>

    @Query("SELECT * FROM house WHERE addressId = :addressId")
    fun getHouseFromAddressId(addressId: Int): Flow<House>

    @Query("SELECT * FROM agent WHERE id = :agentId")
    fun getAgent(agentId: Int): Flow<Agent>

    @Query("SELECT * FROM house")
    fun getAllHouses(): Flow<List<House>>

    @Query("SELECT * FROM address WHERE houseId = :houseId")
    fun getAddressFromHouse(houseId: Int): Flow<Address>

    @Query("SELECT * FROM picture WHERE houseId = :houseId")
    fun getPicturesFromHouse(houseId: Int): Flow<List<Picture>>

    // TRANSACTION HOUSE ADDRESS
    @Transaction
    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseAndAddress(houseId: Int): Flow<List<HouseAndAddress>>

    @Transaction
    @Query("SELECT * FROM house")
    fun getAllHousesAndAddresses(): Flow<List<HouseAndAddress>>

    @Transaction
    @Query("SELECT * FROM house WHERE price BETWEEN :priceMin AND :priceMax AND size BETWEEN :sizeMin AND :sizeMax AND nbrRooms BETWEEN :roomMin AND :roomMax AND nbrBedrooms BETWEEN :bedroomMin AND :bedroomMax AND nbrBathrooms BETWEEN :bathroomMin AND :bathroomMax AND type = :type")
    fun searchHousesAndAddresses(
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
    ): Flow<List<HouseAndAddress>>
    //endregion QUERY

    //region UPDATE
    @Update
    fun updateHouse(house: House)

    @Update
    fun updateAddress(address: Address)
    //endregion UPDATE

    //region DELETE
    @Delete
    fun removePicture(picture: Picture)

    @Delete
    fun removeHouse(house: House)

    @Delete
    fun removeAddress(address: Address)
    //endregion DELETE
}