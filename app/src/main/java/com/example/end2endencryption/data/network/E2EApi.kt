package com.example.end2endencryption.data.network

import retrofit2.Response
import retrofit2.http.*

interface E2EApi {
    @POST("key-exchange/public")
    suspend fun getPublicKey(
        @Header("deviceId") devideId: String,
        @Body body: PublicKeyRequestBodyModel,

        ): Response<PublicKeyResponseModel>

    @POST("key-exchange/check")
    suspend fun checkSecretKey(
        @Header("deviceId") apiKey: String,
        @Body body: CheckSecretKeyRequestBodyModel,

        ): Response<CheckSecretKeyResponseModel>
}