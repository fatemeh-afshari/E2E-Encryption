package com.example.end2endencryption.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.end2endencryption.data.state.DataState
import com.example.end2endencryption.domain.usecase.keyexchange.CheckSecretKeyUseCase
import com.example.end2endencryption.domain.usecase.keyexchange.CreateSecretKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.crypto.SecretKey
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val createSecretKeyUseCase: CreateSecretKeyUseCase,
    private val checkSecretKeyUseCase: CheckSecretKeyUseCase,
) : ViewModel() {

    private val secretKeyState = MutableLiveData<DataState<SecretKey>>()
    val secretKey: LiveData<DataState<SecretKey>>
        get() = secretKeyState

    private val decryptedDataState = MutableLiveData<DataState<String>>()
    val decryptedData: LiveData<DataState<String>>
        get() = decryptedDataState

    init {
        getSecretKey()
    }

    private fun getSecretKey() = viewModelScope.launch {
        createSecretKeyUseCase()
            .onEach { dataState ->
                secretKeyState.value = dataState
            }
            .launchIn(viewModelScope)

    }

    private fun checkSecretKey(plainText: String) = viewModelScope.launch {
        checkSecretKeyUseCase(plainText)
            .onEach { dataState ->
                decryptedDataState.value = dataState
            }
            .launchIn(viewModelScope)

    }


}