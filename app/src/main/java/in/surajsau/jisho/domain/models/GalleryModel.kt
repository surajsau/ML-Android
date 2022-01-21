package `in`.surajsau.jisho.domain.models

sealed class GalleryModel {
    data class Image(val imageFilePath: String, val faceName: String): GalleryModel()
    object Empty: GalleryModel()
}
