package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.dao.HouseDao
import com.openclassrooms.realestatemanager.entities.Address
import com.openclassrooms.realestatemanager.entities.Agent
import com.openclassrooms.realestatemanager.entities.House
import com.openclassrooms.realestatemanager.events.FromListToDetail
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {

    private lateinit var dao: HouseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dao = EstateDatabase.getInstance(this).houseDao
        prepopulateRoomDB()

        setContentView(R.layout.activity_main)
    }

    private fun prepopulateRoomDB() {
        val timestamp = Timestamp(System.currentTimeMillis())

        val addresses = listOf(
                Address(1, "42 Infinite Avenue", "", 42000, "UniverseLand", 1),
                Address(2, "987 Landlord Street", "", 75345, "PlzHelpMeCity", 3),
                Address(3, "123 NoImagination Street", "", 86487, "Imagination", 4),
                Address(4, "456 Blerg rd", "", 34285, "NoIdeaCity", 2),
                Address(5, "404 Unknown Avenue", "404th floor in the huge tower behind the tree. Should be seen from the street.", 24874, "PageNotFound", 5)
        )
        val agents = listOf(
                Agent(1, "Josh", "0601020304", "Josh.Joshy@gmail.com"),
                Agent(2, "Jack", "0602030405", "Jack.Jacky@gmail.com"),
                Agent(3, "Alexandra", "0610203040", "A.lexandra@hotmail.com")
        )
        val houses = listOf(
                House(1, 8450000, "Mansion", 850, 10,
                        "", "", "", true,
                        "27/05/2020", " ", 1, 1),

                House(2, 1325000, "Mansion", 1220, 18,
                        "", "", "", true,
                        "27/05/2020", " ", 2, 4),

                House(3, 650000, "Villa", 350, 6,
                        "", "", "", false,
                        "27/05/2020", "29/06/2021", 3, 2),

                House(4, 1000000, "Villa", 600, 8,
                        "", "", "", true,
                        "27/05/2020", " ", 3, 3),

                House(5, 735000, "Apartment", 250, 5,
                        "", "", "", true,
                        "27/05/2020", " ", 1, 5)
        )

        lifecycleScope.launch {
            addresses.forEach { dao.insertAddress(it) }
            agents.forEach { dao.insertAgent(it) }
            houses.forEach { dao.insertHouse(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    // TODO: 09/07/2021 If tablette --> changer les infos du 2nd frag, if phone, transaction 1st frag to 2nd frag + gestion datas
    @Subscribe
    fun onListClickEvent(event: FromListToDetail) {

        when {
            //Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        val frag: Fragment = findViewById(R.id.list_fragment)

    }
}