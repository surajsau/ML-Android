package `in`.surajsau.jisho.domain.models.chat

import com.google.mlkit.nl.entityextraction.Entity

data class EntityModel(
    val entity: Entity,
    val start: Int,
    val end: Int,
)