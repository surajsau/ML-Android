package `in`.surajsau.jisho.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FaceImage::class], version = AppDb.VERSION)
abstract class AppDb: RoomDatabase() {

    abstract fun faceDao(): FacenetDAO

    companion object {
        const val FILE_NAME = "_ml_android_"
        const val VERSION = 1
    }
}