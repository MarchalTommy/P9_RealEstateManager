package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.addEstate.AddListItemFragment
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((application as EstateApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                // TODO: find a way to get House here to send it to the other fragment as argument. Event ?
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
}