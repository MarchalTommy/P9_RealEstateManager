package com.openclassrooms.realestatemanager.ui.mainList

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.dao.HouseDao
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dao: HouseDao
    private lateinit var thisContext: Context
    private lateinit var housesAndAddress: List<HouseAndAddress>

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

        dao = EstateDatabase.getInstance(thisContext).houseDao
        recyclerView = binding.mainRecycler

        getLocalHouses()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getLocalHouses() {
        val job = lifecycleScope.launch(Dispatchers.IO) {
            Log.d(TAG, "getLocalHouses: Room call started, now fetching houses...")
            withTimeout(3000L) {
                housesAndAddress = dao.getAllHousesAndAddresses()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            job.join()
            Log.d(
                TAG,
                "getLocalHouses: Houses fetched, now preparing the RecyclerView ! ${housesAndAddress.size}"
            )
            prepareAdapter(housesAndAddress)
        }
    }

    private fun prepareAdapter(dataSet: List<HouseAndAddress>) {
        recyclerView.adapter = ListFragmentAdapter(dataSet, ::listOnClick)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }

    private fun listOnClick(houseId: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (Utils.isLandscape(thisContext)) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.second_fragment_twopane, DetailFragment(dao.getHouseWithId(houseId)))
                    .commit()
            } else {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_portrait, DetailFragment(dao.getHouseWithId(houseId)))
                    .commit()
            }
        }
    }
}