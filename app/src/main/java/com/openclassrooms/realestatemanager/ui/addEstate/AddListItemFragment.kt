package com.openclassrooms.realestatemanager.ui.addEstate

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.araujo.jordan.excuseme.ExcuseMe
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.EstateApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.Picture
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddListItemFragment : Fragment() {
    private lateinit var currentPhotoPath: String
    private val pictureList = ArrayList<Picture>()
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var newHouse = House(
        0, " ", 0, 0, 0, 0, " ",
        true, Utils.getTodayDate(), " ", 1, null, " "
    )
    private var address = Address(
        0, " ", " ", 0, " "
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                CoroutineScope(Dispatchers.IO).launch {
                    houseViewModel.removeAddress(address)
                    houseViewModel.removeHouse(newHouse)
                }
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroy() {
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.menu.findItem(R.id.add).isEnabled = true
        toolbar.title = "Real Estate Manager"
        super.onDestroy()
    }

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
        createBaseHouseAndAddress()
        layoutInit()
    }

    private fun createBaseHouseAndAddress() {
        houseViewModel.allHouses.observe(viewLifecycleOwner, {
            if (it != null) {
                address.houseId = it.size + 1
                address.id = it.size + 1
                newHouse.addressId = it.size + 1
                newHouse.houseId = it.size + 1

                lifecycleScope.launch(Dispatchers.IO) {
                    houseViewModel.insertHouse(newHouse)
                    houseViewModel.insertAddress(address)
                }
                houseViewModel.allHouses.removeObservers(viewLifecycleOwner)
            }
        })
    }

    private fun layoutInit() {
        // SETTING BASE LAYOUT
        binding.houseMediaRvDetail.adapter = MediaListAdapter(pictureList)
        binding.houseMediaRvDetail.setHasFixedSize(true)
        binding.newMediaButton.setOnClickListener {
            val items = arrayOf("Camera", "Gallery")
            MaterialAlertDialogBuilder(
                this.requireContext(),
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
            )
                .setCancelable(true)
                .setTitle("Where is the photo ?")
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> {
                            ExcuseMe.couldYouGive(this).permissionFor(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) {
                                if (it.granted.contains(Manifest.permission.CAMERA) &&
                                    it.granted.contains(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                                    it.granted.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                ) {
                                    startCamera()
                                }
                            }
                        }
                        1 -> {
                            ExcuseMe.couldYouGive(this).permissionFor(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) {
                                if (it.granted.contains(Manifest.permission.READ_EXTERNAL_STORAGE))
                                    startGallery()
                            }
                        }
                    }
                }
                .show()
        }
        binding.addFab?.setOnClickListener {
            if (checkData()) {
                checkData()
                addNewHouse()
            }
        }
    }

    private fun addNewHouse() {
        //Adding first picture as main picture and all pics to RoomDatabase
        if (pictureList.isNotEmpty()) {
            newHouse.mainUri = pictureList[0].uri
            for (pic in pictureList) {
                houseViewModel.insertPicture(pic)
            }
        }
        //Updating our database with finished objects
        CoroutineScope(Dispatchers.IO).launch {
            houseViewModel.updateHouse(newHouse)
            houseViewModel.updateAddress(address)
        }
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = "Real Estate Manager"
        Thread.sleep(600)
        //Navigate to the detail of the new house
        if (Utils.isLandscape(requireActivity())) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.second_fragment_twopane, DetailFragment(newHouse))
                .commit()
        } else {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_portrait, DetailFragment(newHouse))
                .commit()
        }
    }

    private fun checkData(): Boolean {
        //Checking important data for validation
        var priceBoolean = false
        var typeBoolean = false
        var sizeBoolean = false
        var wayBoolean = false
        var zipBoolean = false
        var cityBoolean = false
        if (binding.priceLayout.editText?.text.toString().isNotEmpty() &&
            binding.priceLayout.editText?.text.toString().toInt() != 0
        ) {
            newHouse.price = binding.priceLayout.editText!!.text.toString().toInt()
            priceBoolean = true
        } else {
            binding.priceLayout.editText?.error = "You need to enter a price!"
        }
        if (binding.typeLayout.editText?.text.toString().isNotEmpty()) {
            when (binding.typeLayout.editText?.text.toString()) {
                "Mansion" -> {
                    newHouse.type = binding.typeLayout.editText?.text.toString()
                    typeBoolean = true
                }
                "Apartment" -> {
                    newHouse.type = binding.typeLayout.editText?.text.toString()
                    typeBoolean = true
                }
                "House" -> {
                    newHouse.type = binding.typeLayout.editText?.text.toString()
                    typeBoolean = true
                }
                "Villa" -> {
                    newHouse.type = binding.typeLayout.editText?.text.toString()
                    typeBoolean = true
                }
                "Castle" -> {
                    newHouse.type = binding.typeLayout.editText?.text.toString()
                    typeBoolean = true
                }
                else -> binding.typeLayout.editText?.error =
                    "This type of estate is not supported. \nPlease choose between Mansion, Villa, House, Apartment, Castle"
            }
        } else {
            binding.typeLayout.editText?.error =
                "You need to specify the type of estate, between Villa, Apartment, House, and mansion"
        }
        if (binding.surfaceLayout.editText?.text.toString().isNotEmpty() &&
            binding.surfaceLayout.editText?.text.toString().toInt() != 0
        ) {
            newHouse.size = binding.surfaceLayout.editText!!.text.toString().toInt()
            sizeBoolean = true
        } else {
            binding.surfaceLayout.editText?.error = "The surface cannot be null or 0"
        }
        if (binding.locationWayLayout.editText?.text.toString().isNotEmpty()) {
            address.way = binding.locationWayLayout.editText?.text.toString()
            wayBoolean = true
        } else {
            binding.locationWayLayout.editText?.error = "You need to add an address"
        }
        if (binding.locationZipLayout.editText?.text.toString().isNotEmpty() &&
            binding.locationZipLayout.editText?.text.toString().toInt() != 0
        ) {
            address.zip = "${binding.locationZipLayout.editText?.text}".toInt()
            zipBoolean = true
        } else {
            binding.locationZipLayout.editText?.error = "You need to add a zip code"
        }
        if (binding.locationCityLayout.editText?.text.toString().isNotEmpty()) {
            address.city = binding.locationCityLayout.editText?.text.toString()
            cityBoolean = true
        } else {
            binding.locationCityLayout.editText?.error = "You need to add a City"
        }

        //finishing house data in case they're here
        if (binding.bathroomsLayout?.editText?.text.toString().isNotEmpty()) {
            newHouse.nbrBathrooms = binding.bathroomsLayout?.editText?.text.toString().toInt()
        } else newHouse.nbrBathrooms = 0
        if (binding.roomsLayout?.editText?.text.toString().isNotEmpty()) {
            newHouse.nbrRooms = binding.roomsLayout?.editText?.text.toString().toInt()
        } else newHouse.nbrRooms = 0
        if (binding.bedroomsLayout?.editText?.text.toString().isNotEmpty()) {
            newHouse.nbrBedrooms = binding.bedroomsLayout?.editText?.text.toString().toInt()
        } else newHouse.nbrBedrooms = 0
        if (binding.descriptionLayout.editText?.text.toString().isNotEmpty()) {
            newHouse.description = binding.descriptionLayout.editText?.text.toString()
        } else newHouse.description = " "

        return priceBoolean && typeBoolean && sizeBoolean && wayBoolean && zipBoolean && cityBoolean
    }

    private fun pictureListUpdate(picture: Picture) {
        pictureList.add(picture)
        binding.houseMediaRvDetail.adapter?.notifyDataSetChanged()
    }

    // PICTURE STUFF
    // CAMERA
    private fun startCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(
                        requireContext(),
                        "An error has happened while trying to create the new file",
                        Toast.LENGTH_LONG
                    ).show()
                    null
                }
                //Continue only if the file was successfully created :
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    cameraResultLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File = requireActivity().getExternalFilesDir(DIRECTORY_PICTURES)!!
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save the file : path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun galleryAddPic() {
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.toString()), null, null)
    }

    // GALLERY
    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes: ArrayList<String> = ArrayList(3)
        mimeTypes.add("image/jpeg")
        mimeTypes.add("image/png")
        mimeTypes.add("image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        galleryResultLauncher.launch(intent)
    }

    // INTENTS RESULTS (onActivityResult is deprecated)
    private var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent = result.data!!
                mediaDialog(data.data!!)
            }
        }

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                galleryAddPic()
                val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path: String = MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    bitmap,
                    "Title",
                    null
                )
                val uri = Uri.parse(path)
                mediaDialog(uri)
            }
        }

    // DIALOG TO VALIDATE THE PHOTO AND ADD TITLE
    private fun mediaDialog(uri: Uri) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_selected_media, null)
        val picTitleEditText = dialogView.findViewById<EditText>(R.id.pic_title_edit_text)
        val pic = dialogView.findViewById<ImageView>(R.id.select_pic_image_view)

        Glide.with(this)
            .load(uri)
            .into(pic)

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Prepare your picture")
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("Add this picture") { dialog, _ ->
            val newPic = Picture(uri.toString(), picTitleEditText.text.toString(), newHouse.houseId)
            pictureListUpdate(newPic)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}