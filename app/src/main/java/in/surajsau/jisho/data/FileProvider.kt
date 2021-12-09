package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.base.rotate
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class FileProviderImpl @Inject constructor(private val context: Context): FileProvider {

    override fun fetchBitmap(fileName: String): Flow<Bitmap> = flow {
        val cacheDir = context.externalCacheDir ?: error("No external cache found")
        val bitmap = runCatching {
            val imageFile = File(cacheDir, fileName)
            val exif = ExifInterface(imageFile.absolutePath)
            val inputStream = FileInputStream(imageFile)
            val fileBitmap = BitmapFactory.decodeStream(inputStream)

            return@runCatching when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> fileBitmap.rotate(90)
                ExifInterface.ORIENTATION_ROTATE_180 -> fileBitmap.rotate(180)
                ExifInterface.ORIENTATION_ROTATE_270 -> fileBitmap.rotate(270)
                else -> fileBitmap
            }
        }.onFailure { it.printStackTrace() }

        emit(bitmap.getOrElse { error(it) })
    }

    override fun fetchAssetBitmap(fileName: String): Flow<Bitmap> = flow {
        val bitmap = runCatching {
            with(context.assets.open(fileName)) { BitmapFactory.decodeStream(this) }
        }

        emit(bitmap.getOrElse { error(it) })
    }
}

interface FileProvider {

    fun fetchBitmap(fileName: String): Flow<Bitmap>

    fun fetchAssetBitmap(fileName: String): Flow<Bitmap>
}