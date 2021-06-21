package com.openclassrooms.realestatemanager.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class House(
        @PrimaryKey
        val houseId: Int,
        val price: Int,
        val type: String,
        val size: Int,
        val nbrRooms: Int,
        val description: String,
        // TODO: 15/06/2021 Comment photo depuis telephone ? Pas un lien du coup... Et si plusieurs ?
        // Peut être object "photo"... A rechercher
        val pictureURL: String,
        val interestsAround: String,
        val stillAvailable: Boolean,
        val dateEntryOnMarket: String,
        val dateSell: String,
        val agentId: Int,
        val addressId: Int
        ) {
}