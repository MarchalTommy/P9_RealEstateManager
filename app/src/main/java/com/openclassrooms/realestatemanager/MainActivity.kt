package com.openclassrooms.realestatemanager

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.dao.HouseDao
import com.openclassrooms.realestatemanager.entities.Address
import com.openclassrooms.realestatemanager.entities.Agent
import com.openclassrooms.realestatemanager.entities.House
import kotlinx.coroutines.launch
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {

    lateinit var dao: HouseDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dao = EstateDatabase.getInstance(this).houseDao

        prepopulateRoomDB()

        prepareAdapter()
    }

    private fun prepareAdapter() {
        TODO("Not yet implemented")
    }

    private fun prepopulateRoomDB() {
        val timestamp = Timestamp(System.currentTimeMillis())

        val addresses = listOf(
                Address(1, "42 Infinite Avenue", "", 42000, "UniverseLand", 1),
                Address(2, "987 Landlord Street", "", 75345, "PlzHelpMeCity", 3),
                Address(3, "123 NoImagination Street", "", 86487, "Imagination", 4),
                Address(4, "456 Blerg rd", "", 34285, "NoIdeaCity", 2)
        )
        val agents = listOf(
                Agent(1, "Josh", "0601020304", "Josh.Joshy@gmail.com"),
                Agent(2, "Jack", "0602030405", "Jack.Jacky@gmail.com"),
                Agent(3, "Alexandra", "0610203040", "A.lexandra@hotmail.com")
        )
        val houses = listOf(
                House(1, 8450000, "Mansion", 850, 10,
                        "", "", "", true,
                        timestamp,timestamp, 1, 1),
                House(2, 1325000, "Mansion", 1220, 18,
                        "", "", "", true,
                        timestamp,timestamp, 2, 4),
                House(3, 650000, "Villa", 350, 6,
                        "", "", "", false,
                        timestamp,timestamp, 3, 2),
                House(4, 1000000, "Villa", 600, 8,
                        "", "", "", true,
                        timestamp,timestamp, 3, 3),
        )
        lifecycleScope.launch {
            addresses.forEach { dao.insertAddress(it) }
            agents.forEach { dao.insertAgent(it) }
            houses.forEach { dao.insertHouse(it) }
        }
    }
}