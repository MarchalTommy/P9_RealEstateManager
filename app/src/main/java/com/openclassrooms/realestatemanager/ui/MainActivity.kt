package com.openclassrooms.realestatemanager.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val database: EstateDatabase by lazy {
        Room.databaseBuilder(
                this,
                EstateDatabase::class.java, "database"
        ).build()
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
//                navController.navigate(R.id.action_detailFragment_to_Edit)
                true
            }
            R.id.search -> {
//                navController.navigate(R.id.action_listFragment_to_Search)
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
            addresses.forEach { database.houseDao.insertAddress(it) }
            agents.forEach { database.houseDao.insertAgent(it) }
            houses.forEach { database.houseDao.insertHouse(it) }
        }
    }
}