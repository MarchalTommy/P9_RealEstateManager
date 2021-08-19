package com.openclassrooms.realestatemanager.ui.detail

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.Agent
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.edit.EditItemFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailFragment(houseClicked: House) : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private var mHouse: House = houseClicked
    private var address: Address? = null
    private var agent: Agent? = null
    private var isLandscape: Boolean = false

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.menu.findItem(R.id.edit).isEnabled = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.menu.findItem(R.id.edit).isEnabled = true
        toolbar.menu.findItem(R.id.edit).setOnMenuItemClickListener {
            if (Utils.isLandscape(context)) {
                parentFragmentManager.beginTransaction()
//                    .replace(R.id.second_fragment_twopane, EditItemFragment(mHouse))
                    .commit()
                toolbar.menu.findItem(R.id.edit).isEnabled = false
            } else {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_portrait, EditItemFragment(mHouse))
                    .addToBackStack("detail")
                    .commit()
                toolbar.menu.findItem(R.id.edit).isEnabled = false
            }
            true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lateinit var bottomBar: BottomNavigationView
        if (this@DetailFragment.parentFragment != null) {
            Log.d(TAG, "onCreate: ${requireParentFragment().id}")
            bottomBar = requireParentFragment().requireView().findViewById(R.id.bottom_nav_bar)!!
            bottomBar.visibility = View.GONE
        }
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
                if (this@DetailFragment.parentFragment != null) {
                    bottomBar.visibility = View.VISIBLE
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
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

        if (parentFragmentManager.backStackEntryCount > 1) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initLayout() {
        binding.detailAddress.text = address.toString()
        binding.detailType.text = mHouse.type
        binding.detailPrice?.text = mHouse.currencyFormatUS()
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
        binding.detailMediaRv.onFlingListener = null;
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailMediaRv)
    }

    private fun initDataRecyclerView() {
        binding.detailDataRv.adapter = DataDetailAdapter(getDataSet(), getDrawables())
        binding.detailDataRv.setHasFixedSize(true)
        binding.detailDataRv.onFlingListener = null;
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
        val api = resources.getString(R.string.google_api_key)

        fun connectionIsAvailable() {
            staticMapDialog.show()
            val imageView = staticMapDialog.findViewById<ImageView>(R.id.static_map_view)
            Glide.with(requireContext())
                .load(houseViewModel.getStaticMap(addressUrl!!, api))
                .centerCrop()
                .into(imageView!!)
        }

        fun connectionUnavailable() {
            Snackbar.make(
                binding.root,
                "No internet connection available! Please verify that you have access to a network, or try again later.",
                Snackbar.LENGTH_LONG
            ).show()
            // TODO : mettre en place un worker pour prévenir en cas de retour de connection
        }

        binding.detailFab?.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (Utils.isOnline()) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        connectionIsAvailable()
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        connectionUnavailable()
                    }
                }
            }
        }

    }
}