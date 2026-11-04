package br.com.ctech.bluequest.data.challenges

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class UnreadablePhotoException : Exception("Não foi possível ler esta foto.")

@Singleton
class PhotoProcessor @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val resolver = context.contentResolver

    suspend fun jpegData(photo: Uri, maxDimension: Int = 1280, quality: Int = 80): ByteArray =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = decode(photo, maxDimension) ?: throw UnreadablePhotoException()
                val output = ByteArrayOutputStream()

                transformed(bitmap, maxDimension, rotationDegrees(photo)).compress(Bitmap.CompressFormat.JPEG, quality, output)
                output.toByteArray()
            } catch (e: IOException) {
                throw UnreadablePhotoException()
            }
        }

    private fun decode(photo: Uri, maxDimension: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(photo)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        var sampleSize = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= maxDimension) {
            sampleSize *= 2
        }

        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return resolver.openInputStream(photo)?.use { BitmapFactory.decodeStream(it, null, options) }
    }

    private fun rotationDegrees(photo: Uri): Int = resolver.openInputStream(photo)?.use { ExifInterface(it).rotationDegrees } ?: 0

    private fun transformed(bitmap: Bitmap, maxDimension: Int, rotation: Int): Bitmap {
        val scale = minOf(1f, maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height))
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postRotate(rotation.toFloat())
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}