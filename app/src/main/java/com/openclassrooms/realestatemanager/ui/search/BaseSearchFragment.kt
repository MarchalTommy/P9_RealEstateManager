package com.openclassrooms.realestatemanager.ui.search

import android.app.FragmentManager
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding

class BaseSearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
                        .add(R.id.search_container, ListSearchFragment())
                        .commit()
        bottomNav = binding.bottomNavBar

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.listSearch -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.search_container, ListSearchFragment())
                        .commit()
                }
                R.id.mapSearch -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.search_container, MapFragment())
                        .addToBackStack("TEST")
                        .commit()
                }
            }
            true
        }

        bottomNav.setOnItemReselectedListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.menu.findItem(R.id.search).isEnabled = true
        toolbar.title = "Real Estate Manager"
    }
}