package com.saeed.marleyspoon.domain.model

import com.apollographql.apollo3.api.Operation


sealed class BaseResponse< out T > {

    object Loading : BaseResponse<Nothing>()

    data class Error(var error: BaseError) : BaseResponse<Nothing>()

    data class Success<T>(var data: T ) : BaseResponse<T>()
}
