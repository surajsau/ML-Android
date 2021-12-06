package `in`.surajsau.jisho.base

import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.compose.runtime.compositionLocalOf

class BitmapCache {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    private val cache: LruCache<String, Bitmap> = LruCache(maxMemory/8)

    fun save(fileName: String, bitmap: Bitmap) {
        this.cache.put(fileName, bitmap)
    }

    fun has(fileName: String): Boolean = this.cache.get(fileName) != null

    fun get(fileName: String) = this.cache.get(fileName) ?: throw Exception("Couldn't find $fileName")

    fun clear() {
        this.cache.evictAll()
    }

}

val LocalBitmapCache = compositionLocalOf<BitmapCache> { error("BitmapCache instance not provided") }