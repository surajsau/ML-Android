package `in`.surajsau.jisho.di

import `in`.surajsau.jisho.data.DigitalInk
import `in`.surajsau.jisho.data.Translator
import `in`.surajsau.jisho.data.StyleTransfer
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {

        @Provides
        fun provideDigitalInk() = DigitalInk()

        @Provides
        fun translator() = Translator()

        @Provides
        fun provideStyleTransfer(@ApplicationContext context: Context) = StyleTransfer(context)
    }
}