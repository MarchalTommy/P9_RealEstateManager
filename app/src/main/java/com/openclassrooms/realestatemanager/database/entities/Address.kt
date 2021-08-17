package com.openclassrooms.realestatemanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    var way: String,
    var complement: String,
    var zip: Int,
    var city: String,
    val houseId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return if (complement.isEmpty()) {
            "$way\n$zip, $city"
        } else {
            "$way\n$complement\n$zip, $city"
        }
    }

    fun toUrlReadyString(): String {
        return way.replace(" ", "+") + "," + city.replace(" ", "+")
    }
}