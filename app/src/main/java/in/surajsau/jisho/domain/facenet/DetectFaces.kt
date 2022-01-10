package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FaceDetectionProvider
import `in`.surajsau.jisho.data.FileProvider
import android.graphics.Bitmap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DetectFaces @Inject constructor(
    private val faceDetectionProvider: FaceDetectionProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String): Flow<List<String>> {
        return flow { emit(fileProvider.fetchCachedBitmap(fileName)) }
            .flatMapLatest {
                faceDetectionProvider.getFaces(it)
            }
            .map { faceBitmaps ->
                faceBitmaps.map { bitmap -> fileProvider.cacheBitmap(bitmap) }
            }
    }
}