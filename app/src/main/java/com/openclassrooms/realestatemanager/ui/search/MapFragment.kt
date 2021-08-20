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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class MapFragment : Fragment() {
    private lateinit var supportMapFragment: SupportMapFragment
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private val houses = ArrayList<House>()
    private val addresses = ArrayList<Address>()
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
                for (estate in houses) {
                    if (it.snippet == estate.houseId.toString()) {
                        if (Utils.isLandscape(context)) {
                            parentFragmentManager.beginTransaction()
//                                .add(R.id.second_fragment_twopane, DetailFragment(estate.house))
                                .commit()
                        } else {
                            parentFragmentManager.beginTransaction()
                                .add(R.id.search_container, DetailFragment(estate))
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
        houseViewModel.allHouses.observe(viewLifecycleOwner, {
            houses.addAll(it)
            houseViewModel.allAddresses.observe(viewLifecycleOwner, { addressList ->
                addresses.addAll(addressList)
            })
            for (estate in houses) {
                for (address in addresses) {
                    if (address.houseId == estate.houseId) {
                        if (estate.stillAvailable) {
                            val markerOption = MarkerOptions()
                            markerOption.title("AVAILABLE : ${estate.type}")
                                .snippet(estate.houseId.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(41f))
                                .position(
                                    getLocationByAddress(
                                        requireContext(),
                                        "${address.way}, ${address.city}"
                                    )!!
                                )
                            gMap.addMarker(markerOption)
                        } else {
                            val markerOption = MarkerOptions()
                            markerOption.title("SOLD : ${estate.type}")
                                .snippet(estate.houseId.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(189f))
                                .position(
                                    getLocationByAddress(
                                        requireContext(),
                                        "${address.way}, ${address.city}"
                                    )!!
                                )
                            gMap.addMarker(markerOption)
                        }
                    }
                }
            }
        })
    }
}