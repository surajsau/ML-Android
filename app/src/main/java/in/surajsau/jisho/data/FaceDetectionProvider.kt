package `in`.surajsau.jisho.data

import android.graphics.Bitmap
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FaceDetectionProviderImpl @Inject constructor(): FaceDetectionProvider {

    val detector by lazy { FaceDetection.getClient() }

    @ExperimentalGetImage
    override fun getFaces(imageProxy: ImageProxy): Flow<List<Face>> {
        val image = imageProxy.image?.let {
            InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
        }
        return detectFacesFlow(image)
    }

    override fun getFaces(bitmap: Bitmap): Flow<List<Face>> {
        val image = InputImage.fromBitmap(bitmap, 0)
        return detectFacesFlow(image)
    }

    private fun detectFacesFlow(image: InputImage?): Flow<List<Face>> = callbackFlow {
        if (image == null)
            close(Exception("Image not found"))

        detector.process(image!!)
            .addOnSuccessListener { faces -> trySend(faces) }
            .addOnCompleteListener { close() }

        awaitClose { close() }
    }

}

interface FaceDetectionProvider {

    fun getFaces(imageProxy: ImageProxy): Flow<List<Face>>

    fun getFaces(bitmap: Bitmap): Flow<List<Face>>
}