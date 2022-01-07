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

    @Query("SELECT * FROM faceimage WHERE isPrimary = 0")
    suspend fun fetchAllImages(): List<FaceImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFace(face: FaceImage)
}
