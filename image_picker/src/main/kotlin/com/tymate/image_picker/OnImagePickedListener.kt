package com.tymate.image_picker

import android.net.Uri
import java.io.File

interface OnImagePickedListener {
    fun onImagePickerSuccess(imageUri: Uri, file: File, multi: Boolean = false, lastFile: Boolean = false)
    fun onImagePickerError()
}
