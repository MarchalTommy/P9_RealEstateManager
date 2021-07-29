package com.openclassrooms.realestatemanager.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.dao.HouseDao
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.epoxy.DetailDataListEpoxyController
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class DetailFragment(houseClicked: House?) : Fragment() {

    private var mHouse: House? = houseClicked
    private lateinit var address: Address
    private lateinit var dao: HouseDao
    private lateinit var thisContext: Context

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var epoxyController: DetailDataListEpoxyController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = this.requireContext()
        epoxyController = DetailDataListEpoxyController(thisContext)
        dao = EstateDatabase.getInstance(thisContext).houseDao

        getDBData()

        epoxyController.house = mHouse!!
        val epoxyRecyclerView = getView()?.findViewById<EpoxyRecyclerView>(R.id.detail_data_rv)
        epoxyRecyclerView?.setControllerAndBuildModels(epoxyController)

        binding.detailDescription.text = mHouse?.description
        binding.detailAddress.text = address.toString()
    }

    private fun getDBData() {
        runBlocking {
            withTimeout(1000L) {
                address = dao.getAddressFromHouse(mHouse!!.houseId)
            }
        }
    }

    private fun fabStaticMap() {
        val staticMapDialog = MaterialAlertDialogBuilder(
            thisContext,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )
            .setCancelable(true)
            .setTitle("View of ${address.way}")
            .setView(R.layout.dialog_location)

        binding.detailFab?.setOnClickListener {
            staticMapDialog.show()
        }
    }
}