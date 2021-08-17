package com.openclassrooms.realestatemanager.ui.search

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragmentAdapter
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListSearchFragment : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var thisContext: Context
    private var housesAndAddress: ArrayList<HouseAndAddress> = ArrayList()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = this.requireContext()
        recyclerView = binding.mainRecycler

        getLocalHouses()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getLocalHouses() {
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(ContentValues.TAG, "getLocalHouses: Room call started, now fetching houses...")

            houseViewModel.allHousesWithAddress.observe(viewLifecycleOwner, {
                housesAndAddress = it as ArrayList<HouseAndAddress>

                prepareAdapter(housesAndAddress)
            })
        }
    }

    private fun prepareAdapter(dataSet: List<HouseAndAddress>) {
        recyclerView.adapter = ListFragmentAdapter(dataSet, ::listOnClick)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }

    private fun listOnClick(houseId: Int) {
        lateinit var houseClicked: House
        houseViewModel.getHouseWithId(houseId).observe(viewLifecycleOwner, {
            houseClicked = it

            lifecycleScope.launch(Dispatchers.Main) {
//                if (Utils.isLandscape(thisContext)) {
//                    parentFragmentManager.beginTransaction()
//                        .add(R.id.second_fragment_twopane, DetailFragment(houseClicked))
//                        .commit()
//                } else {
//                    parentFragmentManager.beginTransaction()
//                        .replace(R.id.main_fragment_portrait, DetailFragment(houseClicked))
//                        .commit()
//                }
            }
        })


    }
}