package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.DigitalInkProvider
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DownloadDigitalInkIfNeeded @Inject constructor(
    private val digitalInkProvider: DigitalInkProvider
) {

    fun invoke(): Flow<MLKitModelStatus> = digitalInkProvider.checkIfModelIsDownlaoded()
        .flatMapLatest {
            if (it == MLKitModelStatus.Downloaded)
                flowOf(it)
            else
                digitalInkProvider.downloadModel()
        }
}