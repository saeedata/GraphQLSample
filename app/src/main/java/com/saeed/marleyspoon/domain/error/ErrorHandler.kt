package com.saeed.marleyspoon.domain.error

import com.saeed.marleyspoon.domain.model.BaseError

interface ErrorHandler {

    fun getError(throwable: Throwable): BaseError
}