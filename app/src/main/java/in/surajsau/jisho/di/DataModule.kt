package `in`.surajsau.jisho.di

import `in`.surajsau.jisho.data.*
import `in`.surajsau.jisho.data.chat.EntityExtractionProvider
import `in`.surajsau.jisho.data.chat.EntityExtractionpProviderImpl
import `in`.surajsau.jisho.data.chat.SmartRepliesProvider
import `in`.surajsau.jisho.data.chat.SmartRepliesProviderImpl
import `in`.surajsau.jisho.data.db.AppDb
import `in`.surajsau.jisho.data.facenet.*
import android.content.Context
import androidx.room.Room
import com.google.mlkit.nl.entityextraction.internal.EntityExtractorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindTranslatorProvider(provider: TranslatorProviderImpl): TranslatorProvider

    @Binds
    @Singleton
    abstract fun bindDigitalInkProvider(provider: DigitalInkProviderImpl): DigitalInkProvider

    @Binds
    @Singleton
    abstract fun bindScreenDataProvider(provider: ScreensDataProviderImpl): ScreensDataProvider

    @Binds
    @Singleton
    abstract fun bindChatProvider(provider: ChatDataProviderImpl): ChatDataProvider

    @Binds
    @Singleton
    abstract fun bindFaceDetectionProvider(provider: FaceDetectionProviderImpl): FaceDetectionProvider

    @Binds
    @Singleton
    abstract fun bindCardDataProvider(provider: CardDataProviderImpl): CardDataProvider

    @Binds
    @Singleton
    abstract fun bindEntityExtractionProvider(provider: EntityExtractionpProviderImpl): EntityExtractionProvider

    @Binds
    @Singleton
    abstract fun bindSmartRepliesProvider(provider: SmartRepliesProviderImpl): SmartRepliesProvider

    companion object {

        @Provides
        @Singleton
        fun provideFileProvider(@ApplicationContext context: Context): FileProvider = FileProviderImpl(context)

        @Provides
        @Singleton
        fun provideStyleTransfer(@ApplicationContext context: Context): StyleTransferProvider
            = StyleTransferProviderImpl(context)

        @Provides
        @Singleton
        fun provideDb(@ApplicationContext context: Context): AppDb = Room
            .databaseBuilder(context, AppDb::class.java, AppDb.FILE_NAME)
            .build()

        @Provides
        @Singleton
        fun provideFacesProvider(appDb: AppDb): FacesDataProvider = FacesDataProviderImpl(appDb.faceDao())

        @Provides
        @Singleton
        fun proviceFaceRecognitionProvider(
            @ApplicationContext context: Context,
            fileProvider: FileProvider,
        ): FaceRecognitionProvider = FaceRecognitionProviderImpl(context, fileProvider)
    }
}