package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class EditItemFragment(private val house: House) : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutInit()
    }

    private fun layoutInit() {

        // GETTING DATA FROM THE ESTATE CLICKED
        houseViewModel.getHouseWithAddress(house.houseId)
            .observe(viewLifecycleOwner, Observer { houseAndAddress ->
                val house = houseAndAddress[0].house
                val address = houseAndAddress[0].address
                binding.bathroomsPicker.value = house.nbrBathrooms?:0
                binding.bedroomsPicker.value = house.nbrBedrooms?:0
                binding.roomsPicker.value = house.nbrRooms?:0
                binding.surfaceEditText.setText(house.size)
                binding.newDescriptionEditText.setText(house.description)
                binding.locationWayEditText.setText(address.way)
                binding.locationCityEditText.setText(address.city)
                binding.locationZipEditText.setText(address.zip)
            })

        // SETTING BASE LAYOUT
        binding.bathroomsPicker.minValue = 0
        binding.bathroomsPicker.maxValue = 15
        binding.bedroomsPicker.minValue = 0
        binding.bedroomsPicker.maxValue = 25
        binding.roomsPicker.minValue = 0
        binding.roomsPicker.maxValue = 45

        // TODO : implémenter Intent pour media
        binding.newMediaButton.setOnClickListener{

        }

        binding.addFab?.setOnClickListener{
//            val surface = if("$binding.surfaceEditText.text".toInt() == 0 || binding.surfaceEditText.text.isEmpty()){
//                house.size
//            } else {
//                "$binding.surfaceEditText.text".toInt()
//            }
//
//            val rooms = if(binding.roomsPicker.value == 0) {
//                house.nbrRooms
//            } else {
//                binding.roomsPicker.value
//            }

            val house: House = House(house.price, house.type, "$binding.surfaceEditText.text".toInt(),
                binding.roomsPicker.value,binding.bedroomsPicker.value, binding.bathroomsPicker.value,
                "$binding.newDescriptionEditText.text",true, house.dateEntryOnMarket,
                house.dateSell, house.agentId, house.addressId, null)

            // TODO
//            houseViewModel.updateHouse(house)

            if (Utils.isLandscape(this.requireContext())) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.second_fragment_twopane, DetailFragment(house))
                    .commit()
            } else {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_portrait, DetailFragment(house))
                    .commit()
            }
        }
    }

}