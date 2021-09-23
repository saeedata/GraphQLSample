package com.saeed.marleyspoon.utils.extention

import android.util.Log
import com.saeed.marleyspoon.BuildConfig
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.model.BaseResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.toResult(errorHandler: ErrorHandler): Flow<BaseResponse<T>> =
    this.map<T, BaseResponse<T>> {
        if (BuildConfig.DEBUG) {
            delay(2000)
        }
        BaseResponse.Success(it)
    }.onStart {
        emit(BaseResponse.Loading)
    }.catch {
        emit(BaseResponse.Error(errorHandler.getError(it)))
    }
