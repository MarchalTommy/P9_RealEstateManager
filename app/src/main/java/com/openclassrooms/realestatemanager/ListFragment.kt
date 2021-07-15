package com.openclassrooms.realestatemanager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.dao.HouseDao
import com.openclassrooms.realestatemanager.entities.House
import com.openclassrooms.realestatemanager.entities.relations.HouseAndAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListFragment : BaseFragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var dao: HouseDao
    lateinit var thisContext: Context
    lateinit var housesAndAddress: List<HouseAndAddress>

    private lateinit var houses: List<House>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = view!!.context

        dao = EstateDatabase.getInstance(view.context).houseDao
        recyclerView = view.findViewById(R.id.main_recycler)

        getLocalHouses()
    }

    private fun getLocalHouses() {

        GlobalScope.launch {
            housesAndAddress = dao.getAllHousesAndAddresses()

            prepareAdapter(housesAndAddress)
        }
    }

    private fun prepareAdapter(dataSet: List<HouseAndAddress>) {
        recyclerView.adapter = ListFragmentAdapter(dataSet)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }
}