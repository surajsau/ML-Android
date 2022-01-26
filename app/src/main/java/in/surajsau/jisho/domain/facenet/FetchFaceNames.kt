package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.facenet.FacesDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchFaceNames @Inject constructor(
    private val facesDataProvider: FacesDataProvider,
) {

    fun invoke(): Flow<List<String>> = flow {
        val faceNames = facesDataProvider.getFaceNames()
        emit(faceNames)
    }
}