package com.openclassrooms.realestatemanager.ui.mainList

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListFragment : Fragment() {
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListFragmentAdapter
    private lateinit var thisContext: Context
    private var houses: ArrayList<House> = ArrayList()
    private var addresses = ArrayList<Address>()

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

        // Pop off everything up to and including the current tab
//        parentFragmentManager.popBackStackImmediate()
//        childFragmentManager.popBackStackImmediate()

        for (estate in houses) {
            houseViewModel.updateHouse(estate)
        }

        getLocalHouses()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getLocalHouses() {
            Log.d(TAG, "getLocalHouses: Room call started, now fetching houses...")
            houseViewModel.allHouses.observe(viewLifecycleOwner, { it ->
                houses = it as ArrayList<House>
                houseViewModel.allAddresses.observe(viewLifecycleOwner, { addressesList ->
                    addresses = addressesList as ArrayList<Address>
                    prepareAdapter(houses, addresses)
                })
            })
    }

    private fun prepareAdapter(dataSet: List<House>, dataSet2: List<Address>) {
        adapter = ListFragmentAdapter(dataSet, dataSet2, ::listOnClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)

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
                    val estate: House = adapter.getEstateAt(position)
//                    houses.add(estate)

                    if (direction == ItemTouchHelper.RIGHT) {
                        estate.stillAvailable = false
                        estate.dateSell = Utils.getTodayDate()
                        adapter.notifyDataSetChanged()
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate sold successfully.",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("UNDO") {
                                estate.stillAvailable = true
                                estate.dateSell = " "
                                adapter.notifyDataSetChanged()
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
                        adapter.notifyDataSetChanged()
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

    private fun listOnClick(houseId: Int) {
//        var houseClicked: House
        houseViewModel.getHouseWithId(houseId).observe(viewLifecycleOwner, {
//            houseClicked = it
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
}