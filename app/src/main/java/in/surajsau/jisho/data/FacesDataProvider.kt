package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.data.db.FaceImage
import `in`.surajsau.jisho.data.db.FacesDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FacesDataProviderImpl @Inject constructor(private val dao: FacesDAO) : FacesDataProvider {

    override fun getFaces(): Flow<List<FaceImage>> = flow {
        val faces = dao.fetchAllFaces()
        emit(faces)
    }

    override fun getImagesForFace(faceName: String): Flow<List<FaceImage>> = flow {
        val faces = dao.fetchImagesFor(name = faceName)
        emit(faces)
    }

    override fun getAllImages(): Flow<List<FaceImage>> = flow {
        val faces = dao.fetchAllImages()
        emit(faces)
    }

    override suspend fun saveFace(faceName: String, fileName: String, isPrimary: Boolean) {
        val face = FaceImage(isPrimary = false, faceName = faceName, fileName = fileName)
        dao.saveFace(face)
    }
}

interface FacesDataProvider {

    suspend fun saveFace(faceName: String, fileName: String, isPrimary: Boolean)

    fun getFaces(): Flow<List<FaceImage>>

    fun getAllImages(): Flow<List<FaceImage>>

    fun getImagesForFace(faceName: String): Flow<List<FaceImage>>
}