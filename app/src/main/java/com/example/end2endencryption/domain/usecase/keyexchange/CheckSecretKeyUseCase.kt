package com.example.end2endencryption.domain.usecase.keyexchange

import com.example.end2endencryption.data.repository.Repository
import javax.crypto.SecretKey
import javax.inject.Inject

class CheckSecretKeyUseCase @Inject constructor(private val repository: Repository){

    suspend operator fun invoke( plainText: String) = repository.checkSecretKey( plainText)
}