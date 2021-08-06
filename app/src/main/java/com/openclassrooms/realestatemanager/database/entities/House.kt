package com.openclassrooms.realestatemanager.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class House(
        @PrimaryKey
        val houseId: Int,
        val price: Int,
        val type: String,
        val size: Int,
        val nbrRooms: Int,
        val nbrBedrooms: Int,
        val nbrBathrooms: Int,
        val description: String,
        val stillAvailable: Boolean,
        val dateEntryOnMarket: String,
        val dateSell: String,
        val agentId: Int,
        val addressId: Int
        ) : Parcelable