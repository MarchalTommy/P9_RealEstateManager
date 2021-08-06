package com.openclassrooms.realestatemanager.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture

data class HouseAndPicture (
    @Embedded val house: House,
    @Relation(
        parentColumn = "houseId",
        entityColumn = "houseId"
    )
    val picture: Picture
)