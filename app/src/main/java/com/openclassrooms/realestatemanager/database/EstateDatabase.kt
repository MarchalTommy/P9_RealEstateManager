package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.database.dao.HouseDao
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        House::class,
        Agent::class,
        Address::class,
        Picture::class
    ],
    version = 1,
    exportSchema = false

)
abstract class EstateDatabase : RoomDatabase() {

    abstract fun houseDao(): HouseDao

    companion object {
        @Volatile
        private var INSTANCE: EstateDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): EstateDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EstateDatabase::class.java,
                    "estate_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(EstateDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class EstateDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.houseDao())
                }
            }
        }

        suspend fun populateDatabase(houseDao: HouseDao) {
            houseDao.deleteAll()

            val addresses = listOf(
                Address("42 Infinite Avenue", "", 42000, "UniverseLand", 1),
                Address("987 Landlord Street", "", 75345, "PlzHelpMeCity", 3),
                Address("123 NoImagination Street", "", 86487, "Imagination", 4),
                Address("456 Blerg rd", "", 34285, "NoIdeaCity", 2),
                Address("404 Unknown Avenue", "404th floor, no lift.", 24874, "PageNotFound", 5)
            )
            val agents = listOf(
                Agent( "Josh", "0601020304", "Josh.Joshy@gmail.com"),
                Agent( "Jack", "0602030405", "Jack.Jacky@gmail.com"),
                Agent( "Alexandra", "0610203040", "A.lexandra@hotmail.com")
            )
            val houses = listOf(
                House(
                     81450000, "Mansion", 1250, 21,
                    8, 5, " ", true,
                    "27/05/2020", " ", 1, 1
                ),

                House(
                     1325000, "Villa", 650, 12,
                    5, 3, " ", true,
                    "27/05/2020", " ", 2, 4
                ),

                House(
                     650000, "Villa", 350, 6,
                    2, 1, " ", false,
                    "27/05/2020", "29/06/2021", 3, 2
                ),

                House(
                     1000000, "Villa", 600, 8,
                    3, 2, " ", true,
                    "27/05/2020", " ", 3, 3
                ),

                House(
                     735000, "Villa", 250, 5,
                    3, 1, " ", true,
                    "27/05/2020", " ", 1, 5
                )
            )

            addresses.forEach { houseDao.insertAddress(it) }
            agents.forEach { houseDao.insertAgent(it) }
            houses.forEach { houseDao.insertHouse(it) }
        }
    }
}

