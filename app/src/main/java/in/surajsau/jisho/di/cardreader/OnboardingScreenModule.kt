package `in`.surajsau.jisho.di.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.cardreader.GetCardDetails
import `in`.surajsau.jisho.domain.cardreader.processor.IDCardBackProcessor
import `in`.surajsau.jisho.domain.cardreader.processor.IDCardFrontProcessor
import `in`.surajsau.jisho.domain.models.CardDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingScreenModule {

    companion object {

        @Provides
        fun provideGetCardFrontDetails(
            fileProvider: FileProvider,
            cardDataProvider: CardDataProvider
        ): GetCardDetails<CardDetails.Front> {
            return GetCardDetails(
                textProcessor = IDCardFrontProcessor(),
                fileProvider = fileProvider,
                cardDataProvider = cardDataProvider
            )
        }

        @Provides
        fun provideGetCardBackDetails(
            fileProvider: FileProvider,
            cardDataProvider: CardDataProvider
        ): GetCardDetails<CardDetails.Back> {
            return GetCardDetails(
                textProcessor = IDCardBackProcessor(),
                fileProvider = fileProvider,
                cardDataProvider = cardDataProvider
            )
        }
    }

}