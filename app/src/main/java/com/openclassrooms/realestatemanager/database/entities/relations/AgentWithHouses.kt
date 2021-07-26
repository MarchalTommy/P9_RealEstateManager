package com.openclassrooms.realestatemanager.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House

data class AgentWithHouses (
        @Embedded val agent: Agent,
        @Relation(
                parentColumn = "id",
                entityColumn = "agentId"
        )
        val houses: List<House>
)