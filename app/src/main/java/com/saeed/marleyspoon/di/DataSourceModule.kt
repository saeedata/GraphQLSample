package com.saeed.marleyspoon.di

import com.saeed.marleyspoon.data.source.network.apiGraphQL.NetworkRecipeGQLDataSource
import com.saeed.marleyspoon.data.source.network.apiGraphQL.RecipeDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRecipeGQLDataSource(
        recipeDataSource: NetworkRecipeGQLDataSource
    ): RecipeDataSource

}