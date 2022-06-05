package com.example.end2endencryption.data.di

import com.example.end2endencryption.data.factory.createE2EApi
import com.example.end2endencryption.data.network.E2EApi
import com.example.end2endencryption.data.repository.Repository
import com.example.end2endencryption.data.repository.RepositoryImpl
import com.example.end2endencryption.data.service.DeviceID
import com.example.end2endencryption.data.service.SecurityService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideVehicleApi(): E2EApi = createE2EApi()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()
    @Provides
    @Singleton
    fun provideSecurityService(): SecurityService = SecurityService()
    @Provides
    @Singleton
    fun provideDeviceInfo(): DeviceID = DeviceID()
    @Singleton
    @Provides
    fun injectRepo(api: E2EApi, securityService: SecurityService, deviceID: DeviceID) =
        RepositoryImpl(api, securityService, deviceID) as Repository

}