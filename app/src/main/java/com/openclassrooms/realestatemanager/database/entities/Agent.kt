package com.openclassrooms.realestatemanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Agent (
        val name: String,
        val phone: String,
        val mail: String
        ) {
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0

        override fun toString(): String {
                return "Agent in charge : $name\nContact : $mail"
        }
}