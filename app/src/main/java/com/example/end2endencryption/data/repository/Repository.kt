package com.example.end2endencryption.data.repository

import com.example.end2endencryption.data.state.DataState
import kotlinx.coroutines.flow.Flow
import javax.crypto.SecretKey

interface Repository {
    suspend fun  provideSecretKey(): Flow<DataState<SecretKey>>
    suspend fun  checkSecretKey(  plainText: String): Flow<DataState<String>>
}