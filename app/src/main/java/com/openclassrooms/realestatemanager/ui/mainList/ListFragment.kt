package com.openclassrooms.realestatemanager.ui.mainList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.dao.HouseDao
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dao: HouseDao
    private lateinit var thisContext: Context
    private lateinit var housesAndAddress: List<HouseAndAddress>
    private var twoPane = false

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = requireView().context

        dao = EstateDatabase.getInstance(view.context).houseDao
        recyclerView = binding.mainRecycler

        if (Utils.isLandscape(thisContext)) {
            twoPane = true
        }

        getLocalHouses()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getLocalHouses() {

        GlobalScope.launch {
            housesAndAddress = dao.getAllHousesAndAddresses()

            prepareAdapter(housesAndAddress)
        }
    }

    private fun prepareAdapter(dataSet: List<HouseAndAddress>) {
        recyclerView.adapter = ListFragmentAdapter(dataSet, twoPane)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }
}