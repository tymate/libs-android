package com.tymate.image_picker

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import android.provider.MediaStore.MediaColumns
import android.content.ContentResolver
import android.database.Cursor
import com.theartofdev.edmodo.cropper.BuildConfig


/**
 * Created by Aurélien COCQ
 * aurelien@tymate.com
 */
fun Uri.toFile(context: Context): File? {
    return if (isMediaDocument()) {
        return getImageFilePath(context, this)?.run {
            File(this)
        }
    } else {
        File(path)
    }
}

fun File.copyTo(newFile: File): File {
    inputStream().use { input ->
        newFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return newFile
}

private fun getImageFilePath(context: Context, uri: Uri): String? {

//    if (isNewGooglePhotosUri(uri)) {
////        val pathUri = uri.getPath()
////        val newUri = pathUri.substring(pathUri.indexOf("content"), pathUri.lastIndexOf("/ACTUAL"))
//        return getDataColumn(context, uri, null, null)
//    }
    var cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.moveToFirst()
    var image_id = cursor?.getString(0) ?: ""
    image_id = image_id.substring(image_id.lastIndexOf(":") + 1)
    cursor?.close()
    cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", arrayOf<String>(image_id), null)
    cursor?.moveToFirst()
    var path: String? = null
    try {
        path = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
    } catch (e: Exception) {

    }
    cursor?.close()
    return path
}


fun isNewGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.contentprovider" == uri.authority
}

fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = MediaColumns.DATA
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        if (cursor != null) {
            cursor.close()
        }
    }
    return null
}


fun Uri.isMediaDocument(): Boolean {
    return toString().contains("content://") && !toString().contains(BuildConfig.APPLICATION_ID)
}