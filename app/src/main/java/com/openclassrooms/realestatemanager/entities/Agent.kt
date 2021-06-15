package com.openclassrooms.realestatemanager.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Agent (
        @PrimaryKey
        val id: Int,
        val name: String,
        val phone: String,
        val mail: String
        )