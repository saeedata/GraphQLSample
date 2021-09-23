package com.saeed.marleyspoon.data.error

import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.model.BaseError
import java.io.IOException

class ErrorHandlerImpl : ErrorHandler {

    override fun getError(throwable: Throwable): BaseError {
        return when (throwable) {
            is IOException -> BaseError.Network
//            is HttpException -> {
//                when (throwable.code()) {
//                    // no cache found in case of no network, thrown by retrofit -> treated as network error
////                    UNSATISFIABLE_REQUEST -> ErrorEntity.Network
//
//                    // not found
//                    HttpURLConnection.HTTP_NOT_FOUND -> BaseError.NotFound
//
//                    // access denied
//                    HttpURLConnection.HTTP_FORBIDDEN -> BaseError.AccessDenied
//
//                    // unavailable service
//                    HttpURLConnection.HTTP_UNAVAILABLE -> BaseError.ServiceUnavailable
//
//                    // all the others will be treated as unknown error
//                    else -> BaseError.Unknown
//                }
//            }
            else -> BaseError.Unknown
        }
    }
}