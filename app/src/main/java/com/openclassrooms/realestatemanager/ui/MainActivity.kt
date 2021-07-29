package com.openclassrooms.realestatemanager.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val database: EstateDatabase by lazy {
        EstateDatabase.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepopulateRoomDB()



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Setting up the fragments to show (if landscape, Master detail style, else, normal)
        if (Utils.isLandscape(this)) {
            supportFragmentManager.beginTransaction()
                .add(R.id.list_fragment_twopane, ListFragment())
                .commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.second_fragment_twopane, DetailFragment(null))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_portrait, ListFragment())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Menu item click Listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                if (Utils.isTablet(this) || Utils.isLandscape(this)) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.second_fragment_twopane, AddListItemFragment())
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_portrait, AddListItemFragment())
                        .commit()
                }
                item.isEnabled = false
                true
            }
            R.id.edit -> {
                // TODO: find a way to get House here to send it to the other fragment as argument
//                if (Utils.isTablet(this) || Utils.isLandscape(this)) {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.second_fragment_twopane, EditItemFragment())
//                        .commit()
//                } else {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_fragment_portrait, EditItemFragment())
//                        .commit()
//                }
                true
            }
            R.id.search -> {
                // TODO: Implement some kind of search bar like P7 I THINK maybe idk
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Room prepopulation
    private fun prepopulateRoomDB() {

        val addresses = listOf(
            Address(1, "42 Infinite Avenue", "", 42000, "UniverseLand", 1),
            Address(2, "987 Landlord Street", "", 75345, "PlzHelpMeCity", 3),
            Address(3, "123 NoImagination Street", "", 86487, "Imagination", 4),
            Address(4, "456 Blerg rd", "", 34285, "NoIdeaCity", 2),
            Address(
                5,
                "404 Unknown Avenue",
                "404th floor, no lift.",
                24874,
                "PageNotFound",
                5
            )
        )
        val agents = listOf(
            Agent(1, "Josh", "0601020304", "Josh.Joshy@gmail.com"),
            Agent(2, "Jack", "0602030405", "Jack.Jacky@gmail.com"),
            Agent(3, "Alexandra", "0610203040", "A.lexandra@hotmail.com")
        )
        val houses = listOf(
            House(
                1, 81450000, "Mansion", 1250, 21,
                "", " ", "", true,
                "27/05/2020", " ", 1, 1
            ),

            House(
                2, 1325000, "Villa", 650, 12,
                "", " ", "", true,
                "27/05/2020", " ", 2, 4
            ),

            House(
                3, 650000, "Villa", 350, 6,
                "", " ", "", false,
                "27/05/2020", "29/06/2021", 3, 2
            ),

            House(
                4, 1000000, "Villa", 600, 8,
                "", " ", "", true,
                "27/05/2020", " ", 3, 3
            ),

            House(
                5, 735000, "Villa", 250, 5,
                "", " ", "", true,
                "27/05/2020", " ", 1, 5
            )
        )

        runBlocking {
            addresses.forEach { database.houseDao.insertAddress(it) }
            agents.forEach { database.houseDao.insertAgent(it) }
            houses.forEach { database.houseDao.insertHouse(it) }
            Log.d(TAG, "prepopulateRoomDB: Database populated !")
        }

//        lifecycleScope.launch(Dispatchers.IO) {
//
//        }
    }
}