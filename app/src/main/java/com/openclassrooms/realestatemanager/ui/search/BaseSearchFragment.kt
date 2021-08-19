package com.openclassrooms.realestatemanager.ui.search

import android.app.FragmentManager
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.abs

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
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (Utils.isOnline()) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                childFragmentManager.beginTransaction()
                                    .replace(R.id.search_container, MapFragment())
                                    .commit()
                            }
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Snackbar.make(
                                    binding.root,
                                    "No internet connection available! Please verify that you have access to a network, or try again later.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                bottomNav.selectedItemId = R.id.listSearch
                            }
                        }
                    }
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