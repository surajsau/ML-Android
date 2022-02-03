package `in`.surajsau.jisho.data.chat

import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject

class EntityExtractionpProviderImpl @Inject constructor(): EntityExtractionProvider {

    private val extractor by lazy {
        EntityExtraction.getClient(EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build())
    }

    override fun initModel(): Flow<MLKitModelStatus> = callbackFlow {
        trySend(MLKitModelStatus.CheckingDownload)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        extractor.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener { trySend(MLKitModelStatus.Downloaded) }
            .addOnFailureListener {
                it.printStackTrace()
                throw it
            }
            .addOnCompleteListener { close() }

        awaitClose { close() }
    }

    override suspend fun extractEntities(text: String): List<EntityAnnotation> {
        val extractionParams = EntityExtractionParams.Builder(text)
            .build()

        return Tasks.await(extractor.annotate(extractionParams))
    }


}

interface EntityExtractionProvider {

    fun initModel(): Flow<MLKitModelStatus>

    suspend fun extractEntities(text: String): List<EntityAnnotation>
}