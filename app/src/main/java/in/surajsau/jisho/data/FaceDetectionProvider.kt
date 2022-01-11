package `in`.surajsau.jisho.data

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FaceDetectionProviderImpl @Inject constructor(): FaceDetectionProvider {

    val detector by lazy { FaceDetection.getClient() }

    override fun getFaces(bitmap: Bitmap): Flow<List<Bitmap>> {
        val image = InputImage.fromBitmap(bitmap, 0)
        return detectFacesFlow(image)
            .map { faces ->
                faces.map { face ->
                    Bitmap.createBitmap(
                        bitmap,
                        face.boundingBox.left,
                        face.boundingBox.top,
                        face.boundingBox.width(),
                        face.boundingBox.height()
                    )
                }
            }
    }

    private fun detectFacesFlow(image: InputImage?): Flow<List<Face>> = callbackFlow {
        if (image == null)
            close(Exception("Image not found"))

        detector.process(image!!)
            .addOnFailureListener {
                it.printStackTrace()
                throw it
            }
            .addOnSuccessListener { faces -> trySend(faces) }
            .addOnCompleteListener {
                Log.e("Facenet", "Image detection completed")
                close()
            }

        awaitClose { close() }
    }

}

interface FaceDetectionProvider {

    fun getFaces(bitmap: Bitmap): Flow<List<Bitmap>>
}