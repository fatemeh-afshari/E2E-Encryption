package com.example.end2endencryption.data.repository

import com.example.end2endencryption.data.network.CheckSecretKeyRequestBodyModel
import com.example.end2endencryption.data.network.E2EApi
import com.example.end2endencryption.data.network.PublicKeyRequestBodyModel
import com.example.end2endencryption.data.service.DeviceID
import com.example.end2endencryption.data.service.SecurityService
import com.example.end2endencryption.data.state.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.crypto.SecretKey
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: E2EApi,
    private val securityService: SecurityService,
    private val deviceID: DeviceID,
) : Repository {

    override suspend fun provideSecretKey(): Flow<DataState<SecretKey>> = flow {

        emit(DataState.Loading)
        try {
            val keyPair = securityService.generateECKeys()
            val response = api.getPublicKey(deviceID.getUniquePseudoID(),
                PublicKeyRequestBodyModel(securityService.encodeKey(keyPair.public.encoded)))
            if (response.isSuccessful) {
                response.body()?.let {
                    val secretKey = securityService.generateSharedSecret(keyPair.private,
                        securityService.decodePublicKey(it.publicKey))
                    emit(DataState.Success(secretKey))
                } ?: emit(DataState.Error(Exception("Error")))
            } else {
                emit(DataState.Error(Exception("Error")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }

    }

    override suspend fun checkSecretKey(plainText: String): Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            val cipherText = securityService.encrypt("Hello World")
            val response = api.checkSecretKey(deviceID.getUniquePseudoID(),
                CheckSecretKeyRequestBodyModel(cipherText))
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(DataState.Success(it.data))
                } ?: emit(DataState.Error(Exception("Error")))
            } else {
                emit(DataState.Error(Exception("Error")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }


    }
}