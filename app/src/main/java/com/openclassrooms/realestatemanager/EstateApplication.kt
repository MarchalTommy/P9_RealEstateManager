package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.HouseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class EstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EstateDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { HouseRepository(database.houseDao()) }

}