package `in`.surajsau.jisho.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FacesDAO {

    @Query("SELECT * FROM faceimage WHERE isPrimary = 1")
    suspend fun fetchAllFaces(): List<FaceImage>

    @Query("SELECT * FROM faceimage WHERE faceName = :name")
    suspend fun fetchImagesFor(name: String): List<FaceImage>

    @Query("SELECT * FROM faceimage WHERE isPrimary = :isPrimary")
    suspend fun fetchAllImages(isPrimary: Int): List<FaceImage>

    @Query("SELECT DISTINCT faceName FROM faceimage")
    suspend fun getFaceNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFace(face: FaceImage)
}
