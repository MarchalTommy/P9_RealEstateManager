package com.openclassrooms.realestatemanager.ui.addEstate

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.openclassrooms.realestatemanager.ui.mainList.ListFragment
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModel
import com.openclassrooms.realestatemanager.viewmodel.HouseViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddListItemFragment : Fragment() {
    private val GALLERY_REQUEST_CODE = 24
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    private val pictureList = ArrayList<Picture>()
    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val newHouse: House = House(
        0, " ", 0, 0, 0, 0, " ",
        true, Utils.getTodayDate(), " ", 1, 1, " "
    )

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
        layoutInit()
    }

    private fun pictureListUpdate(picture: Picture) {
        pictureList.add(picture)
        binding.houseMediaRvDetail.adapter?.notifyDataSetChanged()
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
                        0 -> startCamera()
                        1 -> startGallery()
                    }
                }
                .show()
        }

        //TODO : add to firestore ?
        binding.addFab?.setOnClickListener {
            addNewHouse()
            //Navigate to the detail of the new house
            if (Utils.isLandscape(this.requireContext())) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.second_fragment_twopane, DetailFragment(newHouse))
                    .commit()
            } else {
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_portrait, DetailFragment(newHouse))
                    .commit()
            }
        }
    }

    // TODO : REWORK FORM VALIDATION + TROUVER POURQUOI NULL EXCEPTION SUR ADDRESS DANS HOUSEANDADDRESS
    private fun addNewHouse() {
        //Creating a new address Object
        val address = Address(
            "${binding.locationWayLayout.editText!!.text}",
            "${binding.locationComplementLayout.editText!!.text}",
            "${binding.locationZipLayout.editText!!.text}".toInt(),
            "${binding.locationCityLayout.editText!!.text}",
            newHouse.houseId
        )
        houseViewModel.insertAddress(address)

        //Adding pictures to RoomDatabase
        if (pictureList.isNotEmpty()) {
            var i = 0
            for (pic in pictureList) {
                if (i == 0) {
                    newHouse.mainUri = pic.uri
                    i = 1
                }
                houseViewModel.insertPicture(pic)
            }
        }

        //Creating the new house
        //Checking dataset for validation
        if (binding.priceLayout.editText.toString()
                .isNotEmpty() && "${binding.priceLayout.editText!!.text}".toInt() != 0
        ) {
            newHouse.price = "${binding.priceLayout.editText!!.text}".toInt()
        } else {
            binding.priceLayout.error = "You need to enter a price!"
        }
        if (binding.typeLayout.editText.toString().isNotEmpty()) {
            when (binding.typeLayout.editText.toString()) {
                "Mansion" -> newHouse.type = binding.typeEditText.toString()
                "Apartment" -> newHouse.type = binding.typeEditText.toString()
                "House" -> newHouse.type = binding.typeEditText.toString()
                "Villa" -> newHouse.type = binding.typeEditText.toString()
                "Castle" -> newHouse.type = binding.typeEditText.toString()
                else -> binding.typeLayout.error =
                    "This type of estate is not supported. \nPlease choose between Mansion, Villa, House, Apartment, Castle."
            }
        } else {
            binding.typeLayout.error = "You need to specify the type of estate."
        }
        if (binding.surfaceLayout.editText.toString()
                .isNotEmpty() && "${binding.surfaceLayout.editText!!.text}".toInt() != 0
        ) {
            newHouse.size = "${binding.surfaceLayout.editText!!.text}".toInt()
        } else {
            binding.surfaceLayout.error = "The surface cannot be null or 0."
        }
        if (binding.roomsLayout!!.editText.toString()
                .isNotEmpty() && "${binding.roomsLayout!!.editText!!.text}".toInt() > 0
        ) {
            newHouse.nbrRooms = "${binding.roomsLayout!!.editText!!.text}".toInt()
        } else {
            binding.roomsLayout!!.error = "The estate must posses more than 0 rooms."
        }
        if (binding.bedroomsLayout!!.editText.toString()
                .isNotEmpty() && "${binding.bedroomsLayout!!.editText!!.text}".toInt() > 0
        ) {
            newHouse.nbrBedrooms = "${binding.bedroomsLayout!!.editText!!.text}".toInt()
        } else {
            binding.bedroomsLayout!!.error = "The estate must have more than 0 bedrooms."
        }
        if (binding.bathroomsLayout!!.editText.toString()
                .isNotEmpty() && "${binding.bathroomsLayout!!.editText!!.text}".toInt() > 0
        ) {
            newHouse.nbrBathrooms = "${binding.bathroomsLayout!!.editText!!.text}".toInt()
        } else {
            binding.bathroomsLayout!!.error = "The estate must posses more than 0 bathrooms."
        }
        if (binding.descriptionLayout.editText.toString().isNotEmpty()) {
            newHouse.description = "${binding.descriptionLayout.editText!!.text}"
        }
        if (newHouse.price > 0 && newHouse.type.isNotEmpty() && newHouse.size > 0 && newHouse.nbrBathrooms > 0) {
            houseViewModel.insertHouse(newHouse)
        }
    }

    private fun checkData() {
        //TODO : trouver meilleur moyen de vérifier le formulaire...
    }

    // PICTURE STUFF
    //CAMERA
    private fun startCamera() {
        if (askForCameraPermissions()) {
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
        } else {
            askForCameraPermissions()
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
        askForStoragePermissions()
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.toString()), null, null)
    }

    //GALLERY
    private fun startGallery() {
        if (askForStoragePermissions()) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes: ArrayList<String> = ArrayList(3)
            mimeTypes.add("image/jpeg")
            mimeTypes.add("image/png")
            mimeTypes.add("image/jpg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            galleryResultLauncher.launch(intent)
        } else {
            askForStoragePermissions()
        }
    }

    //INTENTS RESULTS (onActivityResult is deprecated)
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

    //DIALOG TO VALIDATE THE PHOTO AND ADD TITLE
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

    // PERMISSIONS MANAGEMENT
    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isCameraPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForStoragePermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity() as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity() as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_REQUEST_CODE
                )
            }
            return false
        }
        return true
    }

    private fun askForCameraPermissions(): Boolean {
        if (!isCameraPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity() as Activity,
                    Manifest.permission.CAMERA
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity() as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_IMAGE_CAPTURE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    askForCameraPermissions()
                }
                return
            }
            GALLERY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGallery()
                } else {
                    askForStoragePermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this.requireActivity())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}