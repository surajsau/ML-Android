package `in`.surajsau.jisho.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FacesDAO {

    @Query("SELECT * FROM face WHERE isPrimary = 1")
    suspend fun fetchAllFaces(): List<Face>

    @Query("SELECT * FROM face WHERE faceName = :name")
    suspend fun fetchFacesFor(name: String): List<Face>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFace(face: Face)
}
