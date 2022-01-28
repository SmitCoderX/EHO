package com.driver.eho

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImageCompression(private val context: Context) :
    AsyncTask<String?, Void?, String?>() {
    override fun doInBackground(vararg strings: String?): String? {
        return if (strings.size == 0 || strings[0] == null) null else compressImage(
            strings[0])
    }

    override fun onPostExecute(imagePath: String?) {
        // imagePath is path of new compressed image.
    }

    companion object {
        private const val maxHeight = 1280.0f
        private const val maxWidth = 1280.0f
        fun compressImage(imagePath: String?): String {

            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true

            var bmp = BitmapFactory.decodeFile(imagePath, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

            var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()

            val maxRatio = maxWidth / maxHeight

            if (actualHeight > maxHeight || actualWidth > maxWidth) {

                if (imgRatio < maxRatio) {

                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth) as Int
                    actualHeight = maxHeight.toInt()

                } else if (imgRatio > maxRatio) {

                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight) as Int
                    actualWidth = maxWidth.toInt()

                } else {

                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            try {

                bmp = BitmapFactory.decodeFile(imagePath, options)

            } catch (exception: OutOfMemoryError) {

                exception.printStackTrace()
            }
            try {

                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
            } catch (exception: OutOfMemoryError) {

                exception.printStackTrace()
            }

            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)

            canvas.drawBitmap(bmp!!,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG))
            if (bmp != null) {
                bmp.recycle()
            }
            val exif: ExifInterface
            try {
                exif = ExifInterface(imagePath!!)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap,
                    0,
                    0,
                    scaledBitmap.width,
                    scaledBitmap.height,
                    matrix,
                    true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            val filepath = filename
            try {
                out = FileOutputStream(filepath)

                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return filepath
        }

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int,
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }

        private val filename: String
            private get() {
                @SuppressLint("SimpleDateFormat") val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file =
                    File(Environment.getExternalStorageDirectory().path, ".Tippers")
                if (!file.exists()) {
                    file.mkdirs()
                }
                return file.absolutePath + "/IMG_" + timeStamp + ".jpg"
            }
    }


}