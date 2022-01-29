package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.data.ChatDataProvider
import `in`.surajsau.jisho.data.chat.EntityExtractionProvider
import `in`.surajsau.jisho.data.model.ChatMessageModel
import `in`.surajsau.jisho.domain.models.chat.ChatAnnotation
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import android.util.Log
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.google.mlkit.nl.entityextraction.DateTimeEntity
import com.google.mlkit.nl.entityextraction.Entity
import com.soywiz.klock.DateTime
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FetchLatestMessage @Inject constructor(
    private val chatDataProvider: ChatDataProvider,
    private val entityExtractionProvider: EntityExtractionProvider,
) {

    fun invoke(): Flow<ChatRowModel> {
        return chatDataProvider.latestMessage
            .receiveAsFlow()
            .map {
                when (it) {
                    is ChatMessageModel.Message -> {
                        val annotations = entityExtractionProvider.extractEntities(it.text)
                        val mainText = it.text

                        val annotationMaps = mutableListOf<ChatAnnotation>()

                        val text = buildAnnotatedString {
                            if (annotations.isEmpty()) {
                                append(mainText)
                                return@buildAnnotatedString
                            }

                            val firstAnnotation = annotations.first()

                            annotations.forEachIndexed { index, annotation ->
                                // if first annotation, append text before the first annotation
                                if (index == 0 && annotation.start > 0) {
                                    append(mainText.substring(startIndex = 0, endIndex = firstAnnotation.start))
                                }

                                val entity = annotation.entities.first()
                                val annotatedSubString = mainText.substring(startIndex = annotation.start, endIndex = annotation.end)

                                val chatAnnotation = when {
                                    entity is DateTimeEntity -> {
                                        Optional.of(ChatAnnotation.Reminder(id = "${it.id}:$index", timeStamp = entity.timestampMillis))
                                    }

                                    entity.type == Entity.TYPE_ADDRESS -> {
                                        Optional.of(ChatAnnotation.Address(id = "${it.id}:$index", address = annotation.annotatedText))
                                    }

                                    entity.type == Entity.TYPE_EMAIL -> {
                                        Optional.of(ChatAnnotation.Email(id = "${it.id}:$index", email = annotation.annotatedText))
                                    }

                                    entity.type == Entity.TYPE_PHONE -> {
                                        Optional.of(ChatAnnotation.Phone(id = "${it.id}:$index", phone = annotation.annotatedText))
                                    }

                                    else -> Optional.Empty
                                }

                                if (chatAnnotation is Optional.Some) {
                                    annotationMaps.add(chatAnnotation.data)

                                    pushStringAnnotation(tag = chatAnnotation.data.tag, annotation = mainText)
                                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                        append(annotatedSubString)
                                    }
                                    pop()

                                } else {
                                    append(annotatedSubString)
                                }

                                // if last annotation, append text after the last annotation
                                if (index == annotations.size - 1 && annotation.end < mainText.length - 1) {
                                    append(mainText.substring(startIndex = annotation.end + 1))
                                }
                            }
                        }

                        ChatRowModel.Message(
                            value = text,
                            isLocal = it.isMe,
                            timestamp = DateTime.invoke(it.timeStamp).toString("HH:mm"),
                            annotationMaps = annotationMaps
                        )
                    }

                    is ChatMessageModel.Typing -> ChatRowModel.Typing
                }
            }
    }
}