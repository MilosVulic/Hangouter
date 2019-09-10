package com.metropolitan.hangouterproject.events

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.AppConstants
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.utils.UtilsButtonStyle
import com.metropolitan.hangouterproject.utils.UtilsUIElementsStyle
import kotlinx.android.synthetic.main.fragment_event_adding_second.*
import kotlinx.android.synthetic.main.fragment_event_adding_second.view.*
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject


class EventAddingSecondFragment : Fragment(), View.OnClickListener {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    private var imageUriCapture: Uri? = null
    private var currentPhotoPath: String? = null
    private lateinit var imageName: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as MyApplication).getSettingsComponent().inject(this)
        val view = inflater.inflate(R.layout.fragment_event_adding_second, container, false)
        setButtonStyle(view)
        setDescriptionStyle(view)
        view.buttonContinue2.setOnClickListener(this)
        view.buttonGallery.setOnClickListener(this)
        view.buttonCapture.setOnClickListener(this)
        return view
    }

    // Event Handling for buttons
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonContinue2 -> replaceFragment()
            R.id.buttonGallery -> pickImageFromGallery()
            R.id.buttonCapture -> captureImage()
        }
    }

    // Results from opened intents for results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUriCapture = data?.data
            imageViewThumbnail.visibility = View.VISIBLE
            imageViewThumbnail.setImageBitmap(MediaStore.Images.Media.getBitmap(activity?.contentResolver, data?.data))
        } else if (requestCode == AppConstants.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap =
                MediaStore.Images.Media.getBitmap(context?.contentResolver, Uri.fromFile(File(currentPhotoPath)))
            val scaledBitmap: Bitmap =
                MediaStore.Images.Media.getBitmap(context?.contentResolver, context?.let { getImageUri(it, bitmap) })
            imageUriCapture = Uri.fromFile(File(currentPhotoPath))
            imageViewThumbnail.visibility = View.VISIBLE
            imageViewThumbnail.setImageBitmap(scaledBitmap)
        }
    }

    // Replacing fragments (EventAddingThirdFragment is the new one)
    private fun replaceFragment() {
        val fragment = EventAddingThirdFragment()
        val bundle = Bundle().apply {
            putString("eventName", arguments?.getString("eventName"))
            putString("phoneNumber", arguments?.getString("phoneNumber"))
            arguments?.getInt("capacity")?.let { putInt("capacity", it) }
            putString("address", arguments?.getString("address"))
            putString("latitude", arguments?.getString("latitude"))
            putString("longitude", arguments?.getString("longitude"))
            putParcelable("imageUri", imageUriCapture)
            putString("imageName", imageName)
            putString("description", editTextDescription.text.toString())
        }

        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.constraintLayoutEventAdding, fragment)?.addToBackStack(null)?.commit()
    }

    // Pick image from gallery
    private fun pickImageFromGallery() {
        imageName = UUID.randomUUID().toString()
        startActivityForResult(
            Intent.createChooser(
                Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                "Select image"
            ), AppConstants.PICK_IMAGE_GALLERY
        )
    }

    // Capturing picture with camera
    private fun captureImage() =
        runWithPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            val photoFile: File? = try {
                imageName = UUID.randomUUID().toString()
                createImageFile(imageName)
            } catch (ex: IOException) {
                null
            }
            val photoURI = context?.let {
                photoFile?.let { photoFile ->
                    FileProvider.getUriForFile(
                        it, "com.metropolitan.hangouterproject",
                        photoFile
                    )
                }
            }
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, AppConstants.CAMERA_REQUEST)
        }

    // Setting style to buttons
    private fun setButtonStyle(view: View) {
        context?.let {
            UtilsButtonStyle.setButtonsStyle(
                it,
                preferencesSettings.getBoolean("themeKey", false),
                view.buttonCapture,
                view.buttonContinue2
            )
        }
        context?.let {
            UtilsButtonStyle.setComplementButtonsStyle(
                it,
                preferencesSettings.getBoolean("themeKey", false),
                view.buttonGallery
            )
        }
    }

    // Setting style to description UI element
    private fun setDescriptionStyle(view: View) {
        UtilsUIElementsStyle.setDescriptionBorderStyle(
            preferencesSettings.getBoolean("themeKey", false),
            view.editTextDescription
        )
    }

    // Creating file to store image after capturing
    @Throws(IOException::class)
    private fun createImageFile(imageName: String): File {
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageName, ".jpg", storageDir).apply { currentPhotoPath = absolutePath }
    }

    // Getting image Uri from the bitmap, its needed for later event adding feature
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val outImage: Bitmap = if (inImage.width > inImage.height) {
            Bitmap.createScaledBitmap(inImage, 2560, 1440, true)
        } else {
            Bitmap.createScaledBitmap(inImage, 1440, 2560, true)
        }
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, outImage, "Title", null)
        return Uri.parse(path)
    }

    private fun setPic() {
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            val photoW: Int = outWidth
            val photoH: Int = outHeight
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            ?.also { bitmap -> imageViewThumbnail.setImageBitmap(bitmap) }
    }
}