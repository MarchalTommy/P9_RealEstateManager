package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.dao.HouseDao
import com.openclassrooms.realestatemanager.entities.Address
import com.openclassrooms.realestatemanager.entities.Agent
import com.openclassrooms.realestatemanager.entities.House

@Database(
        entities = [
            House::class,
            Agent::class,
            Address::class
        ],
        version = 1
)
abstract class EstateDatabase : RoomDatabase() {

    abstract val houseDao: HouseDao

    companion object {
        @Volatile
        private var INSTANCE: EstateDatabase? = null

        fun getInstance(context: Context): EstateDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                        context.applicationContext,
                        EstateDatabase::class.java,
                        "estate_db"
                ).build()
                        .also { INSTANCE = it }
            }
        }
    }
}