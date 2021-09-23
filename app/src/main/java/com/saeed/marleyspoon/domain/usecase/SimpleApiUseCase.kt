package com.saeed.marleyspoon.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.model.BaseResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class SimpleApiUseCase<T>(
    private val errorHandler: ErrorHandler,
    private val flow: suspend () -> Flow<BaseResponse<T>>
) : ViewModel() {

    private var scope = CoroutineScope(Job() + viewModelScope.coroutineContext)
    private val _mutableLiveData = MutableLiveData<BaseResponse<T>>()
    val liveData: LiveData<BaseResponse<T>> = _mutableLiveData

    init {
        retry()
    }

    fun retry() {
        scope.launch {
            try {
                launch {
                    flow.invoke()
                        .cancellable()
                        .onEach {
                            _mutableLiveData.value = it
                        }
                        .buffer(1, BufferOverflow.SUSPEND)
                        .collect()
                }
            } catch (ex: CancellationException) {
                _mutableLiveData.value =
                    BaseResponse.Error(errorHandler.getError(ex))
            }
        }
    }

    fun cancel() {
        scope.cancel()
    }

}