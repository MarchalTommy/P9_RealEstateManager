package com.openclassrooms.realestatemanager.ui.search

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.slider.RangeSlider
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragmentAdapter
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ListSearchFragment : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var thisContext: Context
    private lateinit var adapter: ListFragmentAdapter
    private var houses: ArrayList<House> = ArrayList()
    private var addresses: ArrayList<Address> = ArrayList()
    private var filteredDataSetEstate = ArrayList<House>()
    private var filteredDataSetAddress = ArrayList<Address>()

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

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        val isSearch = !toolbar.menu.findItem(R.id.search).isEnabled

        getLocalHouses()
        if (isSearch) {
            setFab()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getLocalHouses() {
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(ContentValues.TAG, "getLocalHouses: Room call started, now fetching houses...")

            houseViewModel.allHouses.observe(viewLifecycleOwner, {
                houses = it as ArrayList<House>
                houseViewModel.allAddresses.observe(viewLifecycleOwner, { addressList ->
                    addresses = addressList as ArrayList<Address>
                    prepareAdapter(houses, addresses)

                })

            })
        }
    }

    private fun prepareAdapter(dataSet: List<House>, dataSet2: List<Address>) {
        adapter = ListFragmentAdapter(dataSet, dataSet2, ::listOnClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }

    private fun filterList(dataSet: List<House>, dataSet2: List<Address>) {
        recyclerView.adapter = ListFragmentAdapter(dataSet, dataSet2, ::listOnClick)
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }

    private fun listOnClick(houseId: Int) {
        lateinit var houseClicked: House
        houseViewModel.getHouseWithId(houseId).observe(viewLifecycleOwner, {
            houseClicked = it
            lifecycleScope.launch(Dispatchers.Main) {
                if (Utils.isLandscape(thisContext)) {
                    parentFragmentManager.beginTransaction()
//                        .add(R.id.second_fragment_twopane, DetailFragment(houseClicked))
                        .commit()
                } else {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.search_container, DetailFragment(houseClicked))
                        .addToBackStack("searchList")
                        .commit()
                }
            }
        })
    }

    private fun setFab() {
        binding.filterFab.visibility = VISIBLE
        recyclerView.addOnScrollListener(FabExtendingOnScrollListener(binding.filterFab))

        binding.filterFab.setOnClickListener {
            filterDialogBuild()
        }
    }

    private fun filterDialogBuild() {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_filter_search, null)
        val priceSlider = dialogView.findViewById<RangeSlider>(R.id.price_slider)
        priceSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            format.format(value.toDouble())
        }
        val sizeSlider = dialogView.findViewById<RangeSlider>(R.id.size_slider)
        sizeSlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter "${value.roundToInt()} sq m"
        }
        var moreRooms = 0
        var moreBedrooms = 0
        var moreBathrooms = 0
        for (h1 in houses) {
            if (moreRooms <= h1.nbrRooms) {
                moreRooms = h1.nbrRooms
            }
            if (moreBedrooms <= h1.nbrBedrooms) {
                moreBedrooms = h1.nbrBedrooms
            }
            if (moreBathrooms <= h1.nbrBathrooms) {
                moreBathrooms = h1.nbrBathrooms
            }
        }
        val roomSlider = dialogView.findViewById<RangeSlider>(R.id.room_slider)
        val bedroomSlider = dialogView.findViewById<RangeSlider>(R.id.bedroom_slider)
        val bathroomSlider = dialogView.findViewById<RangeSlider>(R.id.bathroom_slider)
        priceSlider.values = listOf(10000000.0f, 80000000.0f)
        sizeSlider.values = listOf(350.0f, 2650.0f)
        roomSlider.values = listOf(1f, moreRooms.toFloat())
        bedroomSlider.values = listOf(1f, moreBedrooms.toFloat())
        bathroomSlider.values = listOf(1f, moreBathrooms.toFloat())

        roomSlider.valueTo = moreRooms.toFloat()
        bedroomSlider.valueTo = moreBedrooms.toFloat()
        bathroomSlider.valueTo = moreBathrooms.toFloat()
        val typeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.type_radiogroup)

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Choose your filters")
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("Filter the list") { dialog, _ ->
            val radioButtonId = typeRadioGroup.checkedRadioButtonId
            val radio = typeRadioGroup.findViewById<RadioButton>(radioButtonId)

            if (radio == null) {
                Toast.makeText(
                    requireContext(),
                    "You need to select an estate type !",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                houseViewModel.searchHouse(
                    priceMax = priceSlider.values[1].toInt(),
                    priceMin = priceSlider.values[0].toInt(),
                    sizeMax = sizeSlider.values[1].toInt(),
                    sizeMin = sizeSlider.values[0].toInt(),
                    roomMax = roomSlider.values[1].toInt(),
                    roomMin = roomSlider.values[0].toInt(),
                    bedroomMax = bedroomSlider.values[1].toInt(),
                    bedroomMin = bedroomSlider.values[0].toInt(),
                    bathroomMax = bathroomSlider.values[1].toInt(),
                    bathroomMin = bathroomSlider.values[0].toInt(),
                    type = radio.text as String
                ).observe(viewLifecycleOwner, {
                    filteredDataSetEstate.clear()
                    filteredDataSetAddress.clear()
                    filteredDataSetEstate.addAll(it)
                    for (estate in it) {
                        houseViewModel.getAddressFromHouse(estate.houseId)
                            .observe(viewLifecycleOwner, { addressList ->
                                filteredDataSetAddress.add(addressList)
                                filterList(filteredDataSetEstate, filteredDataSetAddress)
                                dialog.dismiss()
                            })
                    }

                })
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    class FabExtendingOnScrollListener(
        private val floatingActionButton: ExtendedFloatingActionButton
    ) : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                && !floatingActionButton.isExtended
                && recyclerView.computeVerticalScrollOffset() == 0
            ) {
                floatingActionButton.extend()
            }
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy != 0 && floatingActionButton.isExtended) {
                floatingActionButton.shrink()
            }
            super.onScrolled(recyclerView, dx, dy)
        }
    }
}