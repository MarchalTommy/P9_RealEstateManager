package com.openclassrooms.realestatemanager.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class MapFragment(private val localisation: Boolean) : Fragment() {
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var toolbar: MaterialToolbar
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
        supportMapFragment = rootView.findFragment<SupportMapFragment>()
        toolbar = requireActivity().findViewById(R.id.toolbar)
        initMap()

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar.menu.findItem(R.id.map).isEnabled = true
        toolbar.menu.findItem(R.id.add).isEnabled = true
    }

    /*
     SuppressLint because permissions were asked at the click on the map toolbar icon
     if permissions not allowed, setMyLocationEnabled would be false
     */
    @SuppressLint("MissingPermission")
    private fun initMap() {
        supportMapFragment.getMapAsync { googleMap ->
            gMap = googleMap

            getEstateAddress()

            gMap.setMinZoomPreference(4.0f)
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
                                .add(R.id.main_fragment_portrait, DetailFragment(estate.house))
                                .addToBackStack("map")
                                .commit()
                        }
                    }
                }
                false
            }
        }

        if (localisation) {
            gMap.isMyLocationEnabled = localisation
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
        houseViewModel.allHousesAndAddresses.observe(viewLifecycleOwner, {
            housesAndAddresses.addAll(it)
            for (estate in housesAndAddresses) {
                if (estate.house.stillAvailable) {
                    val markerOption = MarkerOptions()
                    markerOption.title("AVAILABLE : ${estate.house.type}")
                        .snippet(estate.house.houseId.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(41f))
                        .position(
                            getLocationByAddress(
                                requireContext(),
                                "${estate.address.way}, ${estate.address.city}"
                            )!!
                        )
                    gMap.addMarker(markerOption)
                } else {
                    val markerOption = MarkerOptions()
                    markerOption.title("SOLD : ${estate.house.type}")
                        .snippet(estate.house.houseId.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(189f))
                        .position(
                            getLocationByAddress(
                                requireContext(),
                                "${estate.address.way}, ${estate.address.city}"
                            )!!
                        )
                    gMap.addMarker(markerOption)
                }
            }
        })
    }
}