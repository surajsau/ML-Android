package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.data.db.Face
import `in`.surajsau.jisho.data.db.FacesDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FacesDataProviderImpl @Inject constructor(private val dao: FacesDAO) : FacesDataProvider {

    override fun getFaces(): Flow<List<Face>> = flow {
        val faces = dao.fetchAllFaces()
        emit(faces)
    }

    override fun getImagesForFace(faceName: String): Flow<List<Face>> = flow {
        val faces = dao.fetchFacesFor(name = faceName)
        emit(faces)
    }

    override suspend fun saveFace(faceName: String, fileName: String) {
        val face = Face(isPrimary = false, faceName = faceName, fileName = fileName)
        dao.saveFace(face)
    }

    override suspend fun saveNewFace(faceName: String, fileName: String) {
        val face = Face(isPrimary = true, faceName = faceName, fileName = fileName)
        dao.saveFace(face)
    }
}

interface FacesDataProvider {

    suspend fun saveFace(faceName: String, fileName: String)
    suspend fun saveNewFace(faceName: String, fileName: String)

    fun getFaces(): Flow<List<Face>>

    fun getImagesForFace(faceName: String): Flow<List<Face>>
}