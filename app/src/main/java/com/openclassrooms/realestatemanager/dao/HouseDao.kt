package com.openclassrooms.realestatemanager.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.entities.Address
import com.openclassrooms.realestatemanager.entities.Agent
import com.openclassrooms.realestatemanager.entities.House
import com.openclassrooms.realestatemanager.entities.relations.AgentWithHouses
import com.openclassrooms.realestatemanager.entities.relations.HouseAndAddress

@Dao
interface HouseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouse(house: House)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent)

    @Query("SELECT * FROM house WHERE addressId = :addressId")
    suspend fun getHouseOfAddress(addressId: Int): List<House>

    @Query("SELECT * FROM House WHERE agentId = :agentId")
    suspend fun getHousesOfAgent(agentId: Int): List<House>

    @Query("SELECT * FROM house WHERE type = :type")
    suspend fun getHouseOfType(type: String): List<House>

    @Query("SELECT * FROM house")
    suspend fun getAllHouses(): List<House>

    @Query("SELECT * FROM address WHERE houseId = :houseId")
    suspend fun getAddressFromHouse(houseId: Int): List<Address>

    @Transaction
    @Query("SELECT * FROM house WHERE houseId = :houseId")
    suspend fun getHouseWithAddress(houseId: Int): List<HouseAndAddress>

    @Transaction
    @Query("SELECT * FROM house")
    suspend fun getAllHousesAndAddresses(): List<HouseAndAddress>
}