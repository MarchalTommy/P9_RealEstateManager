package com.openclassrooms.realestatemanager.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory

class AddListItemFragment : Fragment() {

    private val GALLERY_REQUEST_CODE = 24
    private val REQUEST_CODE = 200
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutInit()
    }

    private fun layoutInit() {

        // SETTING BASE LAYOUT
        binding.bathroomsPicker.minValue = 1
        binding.bathroomsPicker.maxValue = 15
        binding.bedroomsPicker.minValue = 1
        binding.bedroomsPicker.maxValue = 25
        binding.roomsPicker.minValue = 1
        binding.roomsPicker.maxValue = 45

        binding.newMediaButton.setOnClickListener {
            val items = arrayOf("Camera", "Gallery")
            MaterialAlertDialogBuilder(
                this.requireContext(),
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
            )
                .setCancelable(true)
                .setTitle("Where is the photo ?")
                .setView(R.layout.dialog_new_media)
                .setItems(items) { _, which ->
                    when (which) {
                        1 -> startCamera()
                        2 -> startGallery()
                    }
                }
                .show()

//            val builder = AlertDialog.Builder(this.context)
//            with(builder)
//            {
//                setTitle("Where is the photo ?")
//                setItems(items) { dialog, which ->
//                    val x: Int = which
//                    when (x) {
//                        1 -> startCamera()
//                        2 -> startGallery()
//                    }
//                }
//                show()
//            }
        }

        // TODO : Rework layout ajout + edit + ajout nouvel Estate
        binding.addFab?.setOnClickListener {
            val house: House = House(
                50,
                5000,
                "test",
                "${binding.surfaceEditText.text}".toInt(),
                binding.roomsPicker.value,
                binding.bedroomsPicker.value,
                binding.bathroomsPicker.value,
                "$binding.newDescriptionEditText.text",
                true,
                Utils.getTodayDate(),
                " ",
                1,
                1
            )

            houseViewModel.insertHouse(house)

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

    //TODO : gérer les authorisations
    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    //TODO : gérer les authorisations
    private fun startGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes: ArrayList<String> = ArrayList(3)
        mimeTypes.add("image/jpeg")
        mimeTypes.add("image/png")
        mimeTypes.add("image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            //TODO : nouvelle picture (nouvelle dialog avec la photo + un edit text pour le titre)
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null) {
            //TODO : nouvelle picture (diag avec photo + edit text de titre)
        }
    }

}