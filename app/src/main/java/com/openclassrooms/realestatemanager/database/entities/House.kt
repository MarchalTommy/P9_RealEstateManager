package com.openclassrooms.realestatemanager.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class House(
    var price: Int,
    var type: String,
    var size: Int,
    var nbrRooms: Int,
    var nbrBedrooms: Int,
    var nbrBathrooms: Int,
    var description: String,
    var stillAvailable: Boolean,
    var dateEntryOnMarket: String,
    var dateSell: String,
    val agentId: Int,
    var addressId: Int,
    var mainUri: String?
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var houseId: Int = 0

    //TODO : work the price so that it matches a price tag. Regex probably
    fun toString(price: Int): String {
        return ""
    }
}