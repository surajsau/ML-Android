package `in`.surajsau.jisho.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Face(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,

    @ColumnInfo(name = "isPrimary") val isPrimary: Boolean,
    @ColumnInfo(name = "fileName") val fileName: String,
    @ColumnInfo(name = "faceName") val faceName: String
) {
    constructor(isPrimary: Boolean, fileName: String, faceName: String): this(0, isPrimary, fileName, faceName)
}