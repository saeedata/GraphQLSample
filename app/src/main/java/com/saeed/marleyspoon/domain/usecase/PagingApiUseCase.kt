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

class PagingApiUseCase<T>(
    skipInitValue: Int?,
    limitInitValue: Int?,
    private val errorHandler: ErrorHandler,
    private val flow: suspend (skip: Int?, limit: Int?) -> Flow<BaseResponse<T>>
) : ViewModel() {

    private var scope = CoroutineScope(SupervisorJob() + viewModelScope.coroutineContext)
    private val _paginatorSharedFlow =
        MutableSharedFlow<Pair<Int?, Int?>>(1, 0, BufferOverflow.SUSPEND)
    private val _mutableLiveData = MutableLiveData<BaseResponse<T>>()

    val liveData: LiveData<BaseResponse<T>> = _mutableLiveData

    init {
        scope.launch {

            _paginatorSharedFlow.emit(Pair(skipInitValue, limitInitValue))

            _paginatorSharedFlow
                .onEach { paging ->
                    invokeFlow(paging)
                }.collect()

        }
    }

    private fun invokeFlow(paging: Pair<Int?, Int?>) {
        scope.launch {
            try {
                flow.invoke(paging.first, paging.second)
                    .cancellable()
                    .onEach {
                        _mutableLiveData.value = it
                    }
                    .buffer(1, BufferOverflow.SUSPEND)
                    .collect()
            } catch (ex: CancellationException) {
                _mutableLiveData.value =
                    BaseResponse.Error(errorHandler.getError(ex))
            }
        }
    }

    fun nextPage(skip: Int?, limit: Int?) {
        scope.launch {
            _paginatorSharedFlow.emit(Pair(skip, limit))
        }
    }

    fun retry() {
        scope.launch {
            _paginatorSharedFlow
                .shareIn(scope, SharingStarted.Eagerly, 1)
                .onEach { paging ->
                    invokeFlow(paging)
                }.collect()
        }
    }

    fun cancel() {
        scope.cancel()
    }

}