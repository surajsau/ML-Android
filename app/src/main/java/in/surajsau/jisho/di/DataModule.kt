package `in`.surajsau.jisho.di

import `in`.surajsau.jisho.data.*
import `in`.surajsau.jisho.data.chat.ChatProvider
import `in`.surajsau.jisho.data.chat.ChatProviderImpl
import `in`.surajsau.jisho.data.gpt.BpeTokenProvider
import `in`.surajsau.jisho.data.gpt.GPTEncoderProvider
import `in`.surajsau.jisho.data.gpt.GPTProvider
import `in`.surajsau.jisho.data.gpt.GPTProviderImpl
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindTranslatorProvider(provider: TranslatorProviderImpl): TranslatorProvider

    @Binds
    abstract fun bindDigitalInkProvider(provider: DigitalInkProviderImpl): DigitalInkProvider

    @Binds
    abstract fun bindScreenDataProvider(provider: ScreensDataProviderImpl): ScreensDataProvider

    @Binds
    abstract fun bindChatProvider(provider: ChatProviderImpl): ChatProvider

    @Binds
    abstract fun bindGptProvider(provider: GPTProviderImpl): GPTProvider

    companion object {

        @Provides
        fun provideFileProvider(@ApplicationContext context: Context): FileProvider = FileProviderImpl(context)

        @Provides
        fun provideStyleTransfer(@ApplicationContext context: Context): StyleTransferProvider
            = StyleTransferProviderImpl(context)

        @Provides
        fun provideGPTEncoderProvider(fileProvider: FileProvider) = GPTEncoderProvider(fileProvider)

        @Provides
        fun provideBpeTokenProvider(fileProvider: FileProvider) = BpeTokenProvider(fileProvider)
    }
}