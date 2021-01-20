package com.tymate.core.util


import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import io.reactivex.functions.Predicate
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 * on 07/03/15.
 */
object FileUtils {

    private val FILE_DATE_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    var FILE_BASE_DIR = ".rm"

    /* Checks if external storage is available for read and write */
    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    /* Checks if external storage is available to at least read */
    val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }

    fun rename(from: File, to: File): Boolean {
        if (to.exists()) {
            to.delete()
        }
        if (from.exists()) {
            return from.renameTo(to)
        } else {
            Log.d("FilesUtils", "FileUtils.rename !from.exists()")
            return false
        }
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun delete(file: File, predicate: Predicate<File>? = null) {
        if (!file.exists()) {
            return
        }
        if (file.isDirectory) {
            for (child in file.listFiles()) {
                delete(child, predicate)
            }
        }
        try {
            if (predicate == null || predicate.test(file)) {
                file.delete()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    fun getMimeType(path: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        if (extension != null) {
            val mime = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getAppDirectory(context: Context): File? {
        val directory: File
        if (isExternalStorageWritable && isExternalStorageReadable) {
            val externalFilesDir = context.getExternalFilesDir(null) ?: null
            directory = File(externalFilesDir.toString() + File.separator + FILE_BASE_DIR)
        } else {
            directory = File(context.filesDir, FILE_BASE_DIR)
        }
        directory.mkdirs()
        return directory
    }

    //    public static File createFile(Context context, String prefix, String suffix) throws IOException {
    //        // Create an image file name
    //        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(new Date());
    //        String fileName = prefix + timeStamp + "_";
    //        File storageDir = FileUtils.getAppDirectory(context);
    //        // Save a file: path for use with ACTION_VIEW intents
    //        return File.createTempFile(
    //                fileName,  /* prefix */
    //                suffix,         /* suffix */
    //                storageDir      /* directory */
    //        );
    //    }

    @Throws(IOException::class)
    fun createFile(context: Context, prefix: String, suffix: String): File {
        // Create an image file name
        val timeStamp = FILE_DATE_FORMAT.format(Date())
        val file = File(getAppDirectory(context), prefix + timeStamp + suffix)
        if (file.exists()) {
            return file
        }
        file.createNewFile()
        return file
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getRawFilePath(context: Context, name: String): String {
        return "android.resource://" + context.packageName + "/raw/" + name
    }

    @Throws(IOException::class)
    fun initTempFile(context: Context, prefix: String, suffix: String): File {
        // Create an image file name
        val timeStamp = FILE_DATE_FORMAT.format(Date())
        val file = File(getAppDirectory(context), prefix + timeStamp + suffix)
        if (file.exists()) {
            return file
        }
        file.createNewFile()
        return file
    }

    fun readableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }

        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }
}
