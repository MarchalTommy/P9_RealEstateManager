package com.openclassrooms.realestatemanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey
    val id: Int,
    val way: String,
    val complement: String,
    val zip: Int,
    val city: String,
    val houseId: Int
) {
    override fun toString(): String {
        if (complement.equals("")) {
            val string = "${way},\n${zip} $city"
            return string
        } else {
            val string = "${way},\n${complement}\n${zip} $city"
            return string
        }


    }
}