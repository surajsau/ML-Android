package `in`.surajsau.jisho.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
            val inputStream = FileInputStream(imageFile)
            BitmapFactory.decodeStream(inputStream)
        }

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