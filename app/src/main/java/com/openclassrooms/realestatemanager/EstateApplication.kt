package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.HouseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.random.Random

class EstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EstateDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { HouseRepository(database.houseDao()) }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}