package com.saeed.marleyspoon.domain.model

sealed class BaseError {

    object Network : BaseError()

    object NotFound : BaseError()

    object AccessDenied : BaseError()

    object ServiceUnavailable : BaseError()

    object Canceled : BaseError()

    object Unknown : BaseError()
}