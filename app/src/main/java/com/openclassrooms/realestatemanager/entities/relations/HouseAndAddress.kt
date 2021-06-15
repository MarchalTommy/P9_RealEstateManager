package com.openclassrooms.realestatemanager.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.openclassrooms.realestatemanager.entities.Address
import com.openclassrooms.realestatemanager.entities.House

data class HouseAndAddress(
        @Embedded val house: House,
        @Relation(
                parentColumn = "houseId",
                entityColumn = "houseId"
        )
        val address: Address
)