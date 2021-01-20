package com.tymate.image_picker

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Usage: Create new instance, call [.choosePicture] or [.openCamera]
 * override [Activity.onActivityResult], call [.onActivityResult] in it
 * override [Activity.onRequestPermissionsResult], call [.onRequestPermissionsResult] in it
 * get picked file with [.getImageFile]
 *
 *
 * If calling from Fragment, override [Activity.onActivityResult]
 * and call [Fragment.onActivityResult] for your fragment to delegate result
 */
class ImagePicker(private val activity: Activity, private val fragment: Fragment?, private val listener: OnImagePickedListener) {

    companion object {
        private val CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE_WITH_CAMERA = 100
        private val CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE_WITHOUT_CAMERA = 102
        const val DEFAULT_IMAGE_NAME = "photo"
        private const val EXTRA_IMAGE_NAME = "com.tymate.image_picker.ImagePicker:EXTRA_IMAGE_NAME"
        private val FILE_DATE_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    }

    //    private var cropImageUri: Uri? = null
    private var aspectRatioX: Int = 0
    private var aspectRatioY: Int = 0
    private var withCrop: Boolean = false

    private var waitingData: Intent? = null
    private var imageName = DEFAULT_IMAGE_NAME

    fun setWithImageCrop(aspectRatioX: Int, aspectRatioY: Int): ImagePicker {
        withCrop = true
        this.aspectRatioX = aspectRatioX
        this.aspectRatioY = aspectRatioY
        return this
    }

    @JvmOverloads
    @SuppressLint("NewApi")
    fun choosePicture(includeCamera: Boolean, withTime: Boolean? = false) {
        openIntent(true, includeCamera, withTime)
    }

    @JvmOverloads
    @SuppressLint("NewApi")
    fun openCamera(withTime: Boolean? = false) {
        openIntent(false, true, withTime)
    }

    private fun openIntent(includeGallery: Boolean,
                           includeCamera: Boolean,
                           withTimestamp: Boolean? = true) {
        if (withTimestamp == true) {
            val timeStamp = FILE_DATE_FORMAT.format(Date())
            imageName = "${DEFAULT_IMAGE_NAME}_$timeStamp"
        } else {
            imageName = DEFAULT_IMAGE_NAME
        }
        val permissionCode = if (includeGallery) {
            CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE_WITH_CAMERA
        } else {
            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE
        }
        if (includeCamera && requireCameraPermission(permissionCode)) {
            return
        }
        val intent = getPickImageChooserIntent(activity, imageName, includeGallery, includeCamera)

        if (fragment != null) {
            fragment.startActivityForResult(intent, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE)
        } else {
            activity.startActivityForResult(intent, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> handlePickedImageResult(data)
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> handleCroppedImageResult(data)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            imageName = savedInstanceState.getString(EXTRA_IMAGE_NAME) ?: ""
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXTRA_IMAGE_NAME, imageName)
    }

    private fun handleCroppedImageResult(data: Intent?) {
        val result = CropImage.getActivityResult(data)
        val croppedImageUri = result.uri
        listener.onImagePickerSuccess(croppedImageUri, File(croppedImageUri.path))
    }

    //todo peut être faire de l'async
    @SuppressLint("NewApi")
    private fun handlePickedImageResult(data: Intent?) {
        val file: File?
        try {
            file = getPickImageResultFile(activity, data, imageName)
            if (file == null || !file.exists()) {
                listener.onImagePickerError()
                return
            }
        } catch (e: SecurityException) {
            // if catch it's because we can't open this file
            waitingData = data
            requestStoragePermission()
            return
        }
        val imageUri = Uri.fromFile(file)
        if (withCrop) {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(aspectRatioX, aspectRatioY)
                    .start(activity)
        } else {
            listener.onImagePickerSuccess(imageUri, file)
        }
    }

    private fun getCaptureImageOutputUri(context: Context, imageName: String?): Uri {
        val file = getFile(imageName)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.image_picker.provider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun getFile(imageName: String? = DEFAULT_IMAGE_NAME): File {
        if (imageName == null) {
            return File(activity.externalCacheDir, "${DEFAULT_IMAGE_NAME}.jpeg")
        } else {
            return File(activity.externalCacheDir, "$imageName.jpeg")
        }
    }

    private fun getPickImageResultFile(context: Context, data: Intent?, imageName: String?): File? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera || data!!.data == null) getFile(imageName) else data.data?.toFile(context)
    }


    /**
     * Create a chooser intent to select the source to get image from.<br></br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br></br>
     * All possible sources are added to the intent chooser.<br></br>
     * Use "pick_image_intent_chooser_title" string resource to override chooser title.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     */
    private fun getPickImageChooserIntent(context: Context,
                                          imageName: String?,
                                          includeGallery: Boolean,
                                          includeCamera: Boolean): Intent {
        return getPickImageChooserIntent(
                context,
                context.getString(R.string.pick_image_intent_chooser_title),
                includeGallery,
                false,
                includeCamera,
                imageName)
    }


    /**
     * Create a chooser intent to select the source to get image from.<br></br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br></br>
     * All possible sources are added to the intent chooser.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param title the title to use for the chooser UI
     * @param includeDocuments if to include KitKat documents activity containing all sources
     * @param includeCamera if to include camera intents
     */
    private fun getPickImageChooserIntent(
            context: Context,
            title: CharSequence,
            includeGallery: Boolean,
            includeDocuments: Boolean,
            includeCamera: Boolean,
            imageName: String?): Intent {

        val allIntents = ArrayList<Intent>()
        val packageManager = context.packageManager

        // collect all camera intents if Camera permission is available
        if (!CropImage.isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager, imageName))
        }

        if (includeGallery) {
            var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments)
            if (galleryIntents.isEmpty()) {
                galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
            }
            allIntents.addAll(galleryIntents)
        }

        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = allIntents[allIntents.size - 1]
            allIntents.removeAt(allIntents.size - 1)
        }

        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(target, title)

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())

        return chooserIntent
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     */
    private fun getGalleryIntents(packageManager: PackageManager, action: String, includeDocuments: Boolean): List<Intent> {
        val intents = ArrayList<Intent>()
        val galleryIntent = if (action === Intent.ACTION_GET_CONTENT) {
            Intent(action)
        } else {
            Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.`package` = res.activityInfo.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            intents.add(intent)
        }

        // remove documents intent
        if (!includeDocuments) {
            for (intent in intents) {
                if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                    intents.remove(intent)
                    break
                }
            }
        }
        return intents
    }

    private fun getCameraIntents(context: Context, packageManager: PackageManager, imageName: String?): List<Intent> {

        val allIntents = ArrayList<Intent>()
        // Determine Uri of camera image to  save.
        val outputFileUri = getCaptureImageOutputUri(context, imageName)

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.`package` = res.activityInfo.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            allIntents.add(intent)
        }
        return allIntents
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun requireCameraPermission(requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return false
        }
        if (CropImage.isExplicitCameraPermissionRequired(activity)) {
            if (fragment != null) {
                fragment.requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode)
            } else {
                activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode)
            }
            return true
        } else {
            return false
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        if (fragment != null) {
            fragment.requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE),
                    CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
        } else {
            activity.requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE),
                    CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun isGranted(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }
        if (grantResults.size > 1) {

        }
        return grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!isGranted(grantResults)) {
            return
        }
        when (requestCode) {
            CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE_WITH_CAMERA ->
                choosePicture(true, true)
            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE ->
                openCamera(true)
            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE -> {
                val waitingData = waitingData
                if (waitingData != null) {
                    handlePickedImageResult(waitingData)
                    this.waitingData = null
                }
            }
        }
    }
}