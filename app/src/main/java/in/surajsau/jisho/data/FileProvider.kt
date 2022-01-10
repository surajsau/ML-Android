package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.base.getUriForFile
import `in`.surajsau.jisho.base.rotate
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Environment
import android.util.JsonReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.channels.FileChannel
import javax.inject.Inject

class FileProviderImpl @Inject constructor(private val context: Context): FileProvider {

    override suspend fun fetchCachedBitmap(fileName: String): Bitmap {
        val cacheDir = context.externalCacheDir ?: throw Exception("external cache not found")
        val bitmap = getBitmapForFile(File(cacheDir, fileName))
        return bitmap.getOrElse { throw it }
    }

    override suspend fun cacheBitmap(bitmap: Bitmap): String {
        val cacheDir = context.externalCacheDir ?: throw Exception("external cache not found")
        val fileName = "${System.currentTimeMillis()}.jpg"

        val imageFile = File(cacheDir, fileName)

        runCatching {
            val os = imageFile.outputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
        }.onFailure { throw it }

        return fileName
    }

    override suspend fun storeBitmap(folderName: String, fileName: String, bitmap: Bitmap) {
        val storageFolder = File(context.filesDir, folderName)
        if (!storageFolder.exists())
            storageFolder.mkdirs()

        val imageFile = File(storageFolder, fileName)

        val uri = context.getUriForFile(imageFile)
        runCatching {
            val os = context.contentResolver.openOutputStream(uri) ?: throw Exception("Couldn't open OutpuStream")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
        }.onFailure { throw it }
    }

    override fun getCacheFilePath(fileName: String): String {
        val cacheDir = context.externalCacheDir ?: throw Exception("external cache not found")
        return File(cacheDir, fileName).absolutePath
    }

    override fun getFilePath(folderName: String, fileName: String): String {
        val storageFolder = File(context.filesDir, folderName)
        return File(storageFolder, fileName).absolutePath
    }

    override fun fetchAssetBitmap(fileName: String): Flow<Bitmap> = flow {
        val bitmap = runCatching {
            with(context.assets.open(fileName)) { BitmapFactory.decodeStream(this) }
        }

        emit(bitmap.getOrElse { throw it })
    }

    override fun fetchAssetInputStream(fileName: String) = context.assets.open(fileName)

    override fun fetchInterpreter(modelFileName: String): Interpreter {
        val assetFileDescriptor = context.assets.openFd(modelFileName)

        val fileChannel = FileInputStream(assetFileDescriptor.fileDescriptor).channel
        val fileByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)

        return Interpreter(fileByteBuffer, Interpreter.Options().apply { setNumThreads(4) })
    }

    override fun fetchFiles(folderName: String): Flow<List<String>> = flow {
        val storageFolder = File(context.filesDir, folderName)
        if (!storageFolder.exists())
            storageFolder.mkdirs()

        val files = storageFolder.listFiles()
        if (files.isNullOrEmpty())
            emit(emptyList())
        else {
            emit(files.map { it.absolutePath })
        }
    }

    private fun getBitmapForFile(file: File): Result<Bitmap> = runCatching {
            val exif = ExifInterface(file.absolutePath)
            val inputStream = FileInputStream(file)
            val fileBitmap = BitmapFactory.decodeStream(inputStream)

            return@runCatching when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> fileBitmap.rotate(90)
                ExifInterface.ORIENTATION_ROTATE_180 -> fileBitmap.rotate(180)
                ExifInterface.ORIENTATION_ROTATE_270 -> fileBitmap.rotate(270)
                else -> fileBitmap
            }
        }.onFailure { it.printStackTrace() }

    override suspend fun saveEmbeddings(folderName: String, fileName: String, embedding: FloatArray) {
        val storageFolder = File(context.filesDir, folderName)

        if (!storageFolder.exists())
            storageFolder.mkdirs()

        val imageFile = File(storageFolder, fileName)

        val uri = context.getUriForFile(imageFile)
        runCatching {
            val os = context.contentResolver.openOutputStream(uri) ?: throw Exception("Couldn't open OutpuStream")
            val osw = OutputStreamWriter(os)
            osw.write(embedding.joinToString())
            osw.close()
            os.close()
        }.onFailure { throw it }
    }
}

interface FileProvider {

    fun fetchFiles(folderName: String): Flow<List<String>>

    suspend fun fetchCachedBitmap(fileName: String): Bitmap

    fun fetchAssetBitmap(fileName: String): Flow<Bitmap>

    fun fetchAssetInputStream(fileName: String): InputStream

    fun fetchInterpreter(modelFileName: String): Interpreter

    suspend fun cacheBitmap(bitmap: Bitmap): String

    suspend fun storeBitmap(folderName: String, fileName: String, bitmap: Bitmap)

    fun getCacheFilePath(fileName: String): String

    fun getFilePath(folderName: String, fileName: String): String

    suspend fun saveEmbeddings(folderName: String, fileName: String, embedding: FloatArray)
}