package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.facenet.FaceDetectionProvider
import `in`.surajsau.jisho.data.facenet.FaceRecognitionProvider
import `in`.surajsau.jisho.domain.models.FaceRecognitionResult
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DetectFaces @Inject constructor(
    private val faceDetectionProvider: FaceDetectionProvider,
    private val faceRecognitionProvider: FaceRecognitionProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String): Flow<List<FaceRecognitionResult>> {
        return flow { emit(fileProvider.fetchCachedBitmap(fileName)) }
            .flatMapLatest {
                faceDetectionProvider.getFaces(it)
            }
            .map { faceBitmaps ->
                faceBitmaps.map result@ { bitmap ->
                    val faceFileName = fileProvider.cacheBitmap(bitmap)
                    val faceFilePath = fileProvider.getCacheFilePath(faceFileName)
                    val embeddings = faceRecognitionProvider.generateEmbedding(bitmap)

                    val estimateResults = faceRecognitionProvider.compareEmbedding(embeddings)

                    Log.e("Facenet", estimateResults.map { "${it.key}: ${it.value.joinToString()}" }.joinToString("\n"))

                    val averageScores = estimateResults.values.map { scores -> scores.average() }
                    val names = estimateResults.keys.toTypedArray()

                    if (averageScores.maxOrNull() == null)
                        return@result FaceRecognitionResult.NotRecognised(fileName = faceFileName, filePath = faceFilePath)

                    if (averageScores.maxOrNull()!! >= 0.4) {
                        val index = averageScores.indexOf(averageScores.maxOrNull())
                        return@result FaceRecognitionResult.Recognised(
                            fileName = faceFileName,
                            filePath = faceFilePath,
                            estimatedName = names[index]
                        )
                    }

                    return@result FaceRecognitionResult.NotRecognised(fileName = faceFileName, filePath = faceFilePath)
                }

            }
    }
}