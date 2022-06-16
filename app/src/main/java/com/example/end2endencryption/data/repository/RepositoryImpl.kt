package com.example.end2endencryption.data.repository

import android.util.Log
import com.example.end2endencryption.data.network.CheckSecretKeyRequestBodyModel
import com.example.end2endencryption.data.network.E2EApi
import com.example.end2endencryption.data.network.PublicKeyRequestBodyModel
import com.example.end2endencryption.data.service.DeviceID
import com.example.end2endencryption.data.service.SecurityService
import com.example.end2endencryption.data.state.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bouncycastle.util.encoders.Base64
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
            val response = api.getPublicKey(
                deviceID.getUniquePseudoID(),
                PublicKeyRequestBodyModel(securityService.encodeKey(keyPair.public.encoded))
            )
            Log.v("TEST", response.toString())
            Log.v("PUBLIC", "${securityService.encodeKey(keyPair.public.encoded)}")

            if (response.isSuccessful) {
                response.body()?.let {
                    val secretKey = securityService.generateSharedSecret(
                        keyPair.private,
                        securityService.decodePublicKey(it.publicKeySpkiB64)
                    )
                    Log.v("SERVER_HASHED_SHARED_SECRET", it.sharedKey)
                    Log.v("SERVER_SHARED_SECRET", it.notHashedSecretKey)
                    val mySecretKey = String(Base64.encode(secretKey.encoded))
                    Log.v("MY_SHARED_SECRET", mySecretKey)
                    if (mySecretKey == it.sharedKey || mySecretKey == it.notHashedSecretKey) {
                        emit(DataState.Success(secretKey))
                    } else {
                        emit(DataState.Error(Exception("Secret Keys are not equal")))
                    }
                } ?: emit(DataState.Error(Exception("response body is null")))
            } else {

                emit(DataState.Error(Exception(response.toString())))
            }
        } catch (e: Exception) {
            Log.v("TEST", e.message.toString())
            emit(DataState.Error(e))
        }

    }

    override suspend fun checkSecretKey(plainText: String): Flow<DataState<String>> = flow {
        emit(DataState.Loading)
        try {
            val cipherText = securityService.encrypt("Hello World")
            val response = api.checkSecretKey(
                deviceID.getUniquePseudoID(),
                CheckSecretKeyRequestBodyModel(cipherText)
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(DataState.Success(it.data))
                } ?: emit(DataState.Error(Exception("Error")))
            } else {

                emit(DataState.Error(Exception("Error")))
            }
        } catch (e: Exception) {
            Log.v("TEST", e.message.toString())
            emit(DataState.Error(e))
        }


    }
}