package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.data.db.FaceImage
import `in`.surajsau.jisho.data.db.FacesDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FacesDataProviderImpl @Inject constructor(private val dao: FacesDAO) : FacesDataProvider {

    override fun getImagesForFace(faceName: String): Flow<List<FaceImage>> = flow {
        val faces = dao.fetchImagesFor(name = faceName)
        emit(faces)
    }

    override fun getAllImages(isPrimary: Boolean): Flow<List<FaceImage>> = flow {
        val faces = dao.fetchAllImages(isPrimary = if (isPrimary) 1 else 0)
        emit(faces)
    }

    override suspend fun saveFace(faceName: String, fileName: String, isPrimary: Boolean) {
        val face = FaceImage(isPrimary = isPrimary, faceName = faceName, fileName = fileName)
        dao.saveFace(face)
    }

    override suspend fun getFaceNames(): List<String> {
        return dao.getFaceNames()
    }
}

interface FacesDataProvider {

    suspend fun saveFace(faceName: String, fileName: String, isPrimary: Boolean)

    suspend fun getFaceNames(): List<String>

    fun getAllImages(isPrimary: Boolean): Flow<List<FaceImage>>

    fun getImagesForFace(faceName: String): Flow<List<FaceImage>>
}