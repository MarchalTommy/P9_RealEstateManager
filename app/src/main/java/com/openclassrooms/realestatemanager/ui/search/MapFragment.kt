package com.openclassrooms.realestatemanager.ui.search

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class MapFragment : Fragment() {
    private lateinit var supportMapFragment: SupportMapFragment
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private val housesAndAddresses = ArrayList<HouseAndAddress>()
    private lateinit var gMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map_search, container, false)
        supportMapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        initMap()

        return rootView
    }

    private fun initMap() {
        supportMapFragment.getMapAsync { googleMap ->
            gMap = googleMap

            getEstateAddress()

            gMap.setMinZoomPreference(5.0f)
            gMap.setMaxZoomPreference(14.0f)

            val USABounds = LatLngBounds(
                LatLng((25.30), (-125.30)),
                LatLng((48.63), (-60.09))
            )
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(USABounds.center, 5f))

            gMap.setOnMarkerClickListener {
                for (estate in housesAndAddresses) {
                    if (it.snippet == estate.house.houseId.toString()) {
                        if (Utils.isLandscape(context)) {
                            parentFragmentManager.beginTransaction()
//                                .add(R.id.second_fragment_twopane, DetailFragment(estate.house))
                                .commit()
                        } else {
                            parentFragmentManager.beginTransaction()
                                .add(R.id.search_container, DetailFragment(estate.house))
                                .addToBackStack("map")
                                .commit()
                        }
                    }
                }
                false
            }
        }
    }

    private fun getLocationByAddress(context: Context, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        try {
            val address = coder.getFromLocationName(strAddress, 5) ?: return null
            val location = address.first()
            return LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            Log.e(e.toString(), "getLocationByAddress")
        }
        return null
    }

    private fun getEstateAddress() {
        houseViewModel.allHousesWithAddress.observe(viewLifecycleOwner, {
            housesAndAddresses.addAll(it)
            for (estate in housesAndAddresses) {
                val markerOption = MarkerOptions()
                markerOption.title(estate.house.type)
                    .snippet(estate.house.houseId.toString())
                    .position(
                        getLocationByAddress(
                            requireContext(),
                            "${estate.address.way}, ${estate.address.city}"
                        )!!
                    )

                gMap.addMarker(markerOption)
            }
        })
    }
}