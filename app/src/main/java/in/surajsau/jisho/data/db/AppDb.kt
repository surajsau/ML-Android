package `in`.surajsau.jisho.data.db

import `in`.surajsau.jisho.data.db.Face
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Face::class], version = AppDb.VERSION)
abstract class AppDb: RoomDatabase() {

    abstract fun faceDao(): FacesDAO

    companion object {
        const val FILE_NAME = "_ml_android_"
        const val VERSION = 1
    }
}