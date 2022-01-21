package `in`.surajsau.jisho.base

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.getUriForFile(file: File) = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

fun Context.getUriForImage(folderName: String, fileName: String): Uri {
    val file = File(folderName, fileName)
    return getUriForFile(file = file)
}