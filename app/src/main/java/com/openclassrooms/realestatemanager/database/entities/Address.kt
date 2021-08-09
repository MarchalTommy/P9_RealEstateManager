package com.openclassrooms.realestatemanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    val way: String,
    val complement: String,
    val zip: Int,
    val city: String,
    val houseId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return if (complement == "") {
            "$way,\n$zip $city"
        } else {
            "$way,\n$complement\n$zip $city"
        }
    }
}