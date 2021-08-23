package com.openclassrooms.realestatemanager.ui.mainList

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.slider.RangeSlider
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.search.MapFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ListFragment : Fragment() {
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListFragmentAdapter
    private lateinit var thisContext: Context
    private var housesAndAddresses = ArrayList<HouseAndAddress>()
    private var filteredEstates = ArrayList<HouseAndAddress>()
    private var isFiltered = false
    private lateinit var toolbar: MaterialToolbar
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
        toolbar = requireActivity().findViewById(R.id.toolbar)

        for (estate in housesAndAddresses) {
            houseViewModel.updateHouse(estate.house)
        }

        getLocalHouses()
        setFab()
        setItemTouchHelper()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Getting Room data
    private fun getLocalHouses() {
        Log.d(TAG, "getLocalHouses: Room call started, now fetching houses...")
        houseViewModel.allHousesAndAddresses.observe(viewLifecycleOwner, {
            housesAndAddresses.clear()
            housesAndAddresses.addAll(it)
            prepareAdapter(housesAndAddresses)
        })
    }

    //Setting the recyclerView with an ItemTouchHelper for the swipe
    private fun prepareAdapter(dataSet: List<HouseAndAddress>) {
        adapter = ListFragmentAdapter(dataSet, ::listOnClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }

    private fun setItemTouchHelper() {
        //Swipe on item to sold or un-sold it
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val estate: House = adapter.dataSet[position].house
                    if (direction == ItemTouchHelper.RIGHT) {
                        estate.stillAvailable = false
                        estate.dateSell = Utils.getTodayDate()
                        adapter.notifyItemChanged(position)
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate sold successfully.",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("UNDO") {
                                estate.stillAvailable = true
                                estate.dateSell = " "
                                adapter.notifyItemChanged(position)
                                recyclerView.scrollToPosition(position)
                            }
                            setActionTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.primaryColor
                                )
                            )
                        }.show()
                    } else {
                        estate.stillAvailable = true
                        estate.dateSell = " "
                        adapter.notifyItemChanged(position)
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate successfully marked as not sold.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    houseViewModel.updateHouse(estate)
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    private fun filterList(dataSet: List<HouseAndAddress>) {
        adapter.dataSet = dataSet
        adapter.notifyDataSetChanged()
    }

    //OnClick for the items of the recyclerView
    private fun listOnClick(houseId: Int) {
        houseViewModel.getHouseWithId(houseId).observe(viewLifecycleOwner, {
            if (Utils.isLandscape(thisContext)) {
//                parentFragmentManager.beginTransaction()
//                    .add(R.id.second_fragment_twopane, DetailFragment(houseClicked))
//                    .commit()
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_portrait, DetailFragment(it))
                    .addToBackStack("list")
                    .commit()

            }
        })
    }

    //Setting up the filter button
    private fun setFab() {
        binding.filterFab.visibility = View.VISIBLE
        recyclerView.addOnScrollListener(FabExtendingOnScrollListener(binding.filterFab))
        binding.filterFab.setOnClickListener {
            if (isFiltered) {
                filterList(housesAndAddresses)
                isFiltered = false
                changeFabState()
            } else {
                filterDialogBuild()
            }
        }
    }

    //Preparing and showing the filter Dialog
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
        for (estate in housesAndAddresses) {
            if (moreRooms <= estate.house.nbrRooms) {
                moreRooms = estate.house.nbrRooms
            }
            if (moreBedrooms <= estate.house.nbrBedrooms) {
                moreBedrooms = estate.house.nbrBedrooms
            }
            if (moreBathrooms <= estate.house.nbrBathrooms) {
                moreBathrooms = estate.house.nbrBathrooms
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
                houseViewModel.searchHouseAndAddress(
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
                    filteredEstates.clear()
                    filteredEstates.addAll(it)
                    filterList(filteredEstates)
//                    housesAndAddresses.clear()
//                    housesAndAddresses.addAll(it)
//                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                    isFiltered = true
                    changeFabState()
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

    private fun changeFabState() {
        if (isFiltered) {
            binding.filterFab.icon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.outline_cancel_black_24dp
            )
            binding.filterFab.text = "Remove filters"
        } else {
            binding.filterFab.icon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_filter_24dp
            )
            binding.filterFab.text = "Add filters"
        }
    }

    //OnScrollListener for the filter extended fab
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