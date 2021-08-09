package com.openclassrooms.realestatemanager.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.epoxy.DetailDataListEpoxyController
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class DetailFragment(houseClicked: House?) : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private var mHouse: House? = houseClicked
    private lateinit var address: Address
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

        getDBData()

        epoxyController.house = mHouse!!
        val epoxyRecyclerView = getView()?.findViewById<EpoxyRecyclerView>(R.id.detail_data_rv)
        epoxyRecyclerView?.setControllerAndBuildModels(epoxyController)

        binding.detailDescription.text = mHouse?.description



    }

    private fun getDBData() {
        houseViewModel.getAddressFromHouse(mHouse!!.houseId).observe(viewLifecycleOwner, {
            address = it
            binding.detailAddress.text = address.toString()
            fabStaticMap()
        })
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