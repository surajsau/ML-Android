package `in`.surajsau.jisho.di.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.cardreader.GetCardDetails
import `in`.surajsau.jisho.domain.cardreader.processor.CCFrontProcessor
import `in`.surajsau.jisho.domain.cardreader.processor.IDCardBackProcessor
import `in`.surajsau.jisho.domain.cardreader.processor.IDCardFrontProcessor
import `in`.surajsau.jisho.domain.models.CreditCard
import `in`.surajsau.jisho.domain.models.IDCard
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingScreenModule {

    companion object {

        @Provides
        fun provideCCFrontDetails(
            fileProvider: FileProvider,
            cardDataProvider: CardDataProvider
        ): GetCardDetails<CreditCard.Front> {
            return GetCardDetails(
                textProcessor = CCFrontProcessor(),
                fileProvider = fileProvider,
                cardDataProvider = cardDataProvider
            )
        }

        @Provides
        fun provideGetCardFrontDetails(
            fileProvider: FileProvider,
            cardDataProvider: CardDataProvider
        ): GetCardDetails<IDCard.Front> {
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
        ): GetCardDetails<IDCard.Back> {
            return GetCardDetails(
                textProcessor = IDCardBackProcessor(),
                fileProvider = fileProvider,
                cardDataProvider = cardDataProvider
            )
        }
    }

}