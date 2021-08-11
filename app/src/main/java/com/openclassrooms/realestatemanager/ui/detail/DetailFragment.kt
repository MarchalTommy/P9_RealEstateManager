package com.openclassrooms.realestatemanager.ui.detail

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.EditItemFragment
import com.openclassrooms.realestatemanager.ui.mainList.ListFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class DetailFragment(houseClicked: House) : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private val mHouse: House = houseClicked
    private var address: Address? = null
    private var agent: Agent? = null
    private var isLandscape: Boolean = false

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            if (Utils.isLandscape(context)) {
                parentFragmentManager.beginTransaction()
//                    .add(R.id.second_fragment_twopane, DetailFragment(null))
                    .commit()
            } else {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_portrait, ListFragment())
                    .commit()
            }
        }
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.menu.findItem(R.id.edit).isEnabled = true
        toolbar.menu.findItem(R.id.edit).setOnMenuItemClickListener {
            if (Utils.isLandscape(context)) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.second_fragment_twopane, EditItemFragment(mHouse))
                    .commit()
                toolbar.menu.findItem(R.id.edit).isEnabled = false
            } else {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_portrait, EditItemFragment(mHouse))
                    .commit()
                toolbar.menu.findItem(R.id.edit).isEnabled = false
            }
            true
        }
        callback.isEnabled = true
    }

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
        isLandscape = Utils.isLandscape(requireContext())

        getDBData()

        binding.detailDescription.text = mHouse.description

        initDataRecyclerView()
    }

    private fun initLayout() {
        binding.detailAddress.text = address.toString()
        binding.detailType.text = mHouse.type
        binding.detailPrice?.text = mHouse.price.toString()
    }

    private fun finishLayout() {
        binding.detailAgent.text = agent!!.toString()
    }

    private fun getDrawables(): List<Drawable> {
        val sizeDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_surface)
        val roomDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_room)
        val bedroomDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bedroom)
        val bathroomDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bathroom)
        return listOf(sizeDrawable!!, roomDrawable!!, bedroomDrawable!!, bathroomDrawable!!)
    }

    private fun getDataSet(): List<Int> {
        val size = mHouse.size
        val nbrRooms = mHouse.nbrRooms
        val nbrBedrooms = mHouse.nbrBedrooms
        val nbrBathrooms = mHouse.nbrBathrooms
        return listOf(size, nbrRooms, nbrBedrooms, nbrBathrooms)
    }

    private fun getDBData() {
        houseViewModel.getAddressFromHouse(mHouse.houseId).observe(viewLifecycleOwner, {
            address = it
            initLayout()
            fabStaticMap()
        })
        houseViewModel.getPictures(mHouse.houseId).observe(viewLifecycleOwner, {
            initMediaRecyclerView(it)
        })
        houseViewModel.getAgent(mHouse.agentId).observe(viewLifecycleOwner, {
            agent = it
            finishLayout()
        })
    }

    private fun initMediaRecyclerView(dataSet: List<Picture>) {
        binding.detailMediaRv.adapter = PictureListAdapter(dataSet, isLandscape)
        binding.detailMediaRv.setHasFixedSize(true)

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailMediaRv)
    }

    private fun initDataRecyclerView() {
        binding.detailDataRv.adapter = DataDetailAdapter(getDataSet(), getDrawables())
        binding.detailDataRv.setHasFixedSize(true)

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailDataRv)

    }

    private fun fabStaticMap() {
        val staticMapDialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )
            .setCancelable(true)
            .setView(R.layout.dialog_location)
            .create()

        staticMapDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val addressUrl = address?.toUrlReadyString()
        Log.d(TAG, "fabStaticMap: TEST ADDRESS :$addressUrl")
        val api = "AIzaSyCSY3FEI2LunSxhfgak7UOK3lFrjdzvFg4"
        val url =
            "https://maps.googleapis.com/maps/api/staticmap?center=${addressUrl}&zoom=15&size=300x300&scale=3&markers=color:red|${addressUrl}&key=${api}"

        binding.detailFab?.setOnClickListener {
            staticMapDialog.show()
            val imageView = staticMapDialog.findViewById<ImageView>(R.id.static_map_view)


            Glide.with(requireContext())
                .load(url)
                .centerCrop()
                .into(imageView!!)
        }
    }
}