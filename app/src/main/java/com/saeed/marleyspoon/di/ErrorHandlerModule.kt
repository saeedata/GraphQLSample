package com.saeed.marleyspoon.di

import com.saeed.marleyspoon.data.error.ErrorHandlerImpl
import com.saeed.marleyspoon.domain.error.ErrorHandler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
 class ErrorHandlerModule {

//    @Binds
//    @Singleton
//    abstract fun bindErrorHandler(
//        errorHandlerImpl: ErrorHandlerImpl
//    ): ErrorHandler


    @Provides
    @Singleton
    fun bindErrorHandler(
    ): ErrorHandler {
        return ErrorHandlerImpl()
    }
}