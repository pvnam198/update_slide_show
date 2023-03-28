package com.example.slide.di

import android.app.Application
import android.content.Context
import com.example.slide.notify.NotificationRepository
import com.example.slide.notify.NotificationSetter
import com.example.slide.notify.NotificationShared
import com.example.slide.util.BillingConstants
import com.playbilling.BillingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationShared(application: Application) = NotificationShared(application)

    @Provides
    @Singleton
    fun provideNotificationRepository(notificationShared: NotificationShared) =
        NotificationRepository(notificationShared)

    @Provides
    @Singleton
    fun provideNotificationSetter(notificationRepository: NotificationRepository) =
        NotificationSetter(notificationRepository)

    @Provides
    @Singleton
    fun provideBillingRepository(@ApplicationContext context: Context) =
        BillingRepository(context, BillingConstants.SUBS, BillingConstants.SUBS, emptyList())

}