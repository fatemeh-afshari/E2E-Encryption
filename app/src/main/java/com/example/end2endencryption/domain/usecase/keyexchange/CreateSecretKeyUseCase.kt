package com.example.end2endencryption.domain.usecase.keyexchange

import com.example.end2endencryption.data.repository.Repository
import javax.inject.Inject

class CreateSecretKeyUseCase @Inject constructor(private val repository: Repository){

    suspend operator fun invoke() = repository.provideSecretKey()
}