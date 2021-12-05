package `in`.surajsau.jisho.di

import `in`.surajsau.jisho.data.*
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

    companion object {

        @Provides
        fun provideFileProvider(@ApplicationContext context: Context): FileProvider = FileProviderImpl(context)

        @Provides
        fun provideStyleTransfer(@ApplicationContext context: Context) = StyleTransfer(context)
    }
}