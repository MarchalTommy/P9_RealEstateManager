package com.openclassrooms.realestatemanager.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House

data class HouseAndAddress(
        @Embedded val house: House,
        @Relation(
                parentColumn = "houseId",
                entityColumn = "houseId"
        )
        val address: Address
)