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

    @Transaction
    @Query("SELECT * FROM house WHERE addressId = :addressId")
    suspend fun getHouseWithAddress(addressId: Int): List<HouseAndAddress>

    @Transaction
    @Query("SELECT * FROM house WHERE agentId = :agentId")
    suspend fun getHousesWithAgent(agentId: Int): List<AgentWithHouses>

    @Query("SELECT * FROM house WHERE type = :type")
    suspend fun getHouseWithType(type: String): List<House>
}