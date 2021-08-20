package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.addEstate.AddListItemFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragment
import com.openclassrooms.realestatemanager.ui.search.BaseSearchFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar
    private val menuItems = ArrayList<MenuItem>()

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((application as EstateApplication).repository)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Setting up the fragments to show (if landscape, Master detail style, else, normal)
        if (Utils.isLandscape(this)) {
            supportFragmentManager.beginTransaction()
                .add(R.id.list_fragment_twopane, ListFragment())
                .commit()
            supportFragmentManager.beginTransaction()
//                .add(R.id.second_fragment_twopane, DetailFragment(null))
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
        toolbar.menu[1].isEnabled = false
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
                    toolbar.title = "Add a new estate"
                } else {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.main_fragment_portrait, AddListItemFragment())
                        .addToBackStack("start")
                        .commit()
                    toolbar.title = "Add a new estate"
                }
                menuItems.add(item)
                item.isEnabled = false
                true
            }
            R.id.edit -> {
                menuItems.add(item)
                item.isEnabled = false
                true
            }
            R.id.search -> {
                // TODO: Implement some kind of search bar like P7 I THINK maybe idk
                if (Utils.isTablet(this) || Utils.isLandscape(this)) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.second_fragment_twopane, BaseSearchFragment())
                        .commit()
                    toolbar.title = "Search"
                } else {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.main_fragment_portrait, BaseSearchFragment())
                        .addToBackStack("start")
                        .commit()
                    toolbar.title = "Search"
                }
                menuItems.add(item)
                item.isEnabled = false
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}