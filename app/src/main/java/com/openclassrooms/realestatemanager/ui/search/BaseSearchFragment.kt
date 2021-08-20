package com.openclassrooms.realestatemanager.ui.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                            ExcuseMe.couldYouGive(requireContext()).permissionFor(
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) {
                                if (it.granted.contains(android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
                                    it.granted.contains(android.Manifest.permission.ACCESS_FINE_LOCATION)
                                ) {
                                    //TODO LANCER LA LOCALISATION DE L'USER
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        childFragmentManager.beginTransaction()
                                            .replace(R.id.search_container, MapFragment())
                                            .commit()
                                    }
                                }

                            }
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Snackbar.make(
                                    binding.root,
                                    "No internet connection available! Please verify that you have access to a network, or try again later.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                bottomNav.selectedItemId = R.id.listSearch
                                // TODO : mettre en place un worker pour prévenir en cas de retour de connection
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