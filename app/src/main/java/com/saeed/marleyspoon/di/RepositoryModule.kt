package com.saeed.marleyspoon.di

import com.saeed.marleyspoon.data.repository.RecipeRepositoryIml
import com.saeed.marleyspoon.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepository: RecipeRepositoryIml
    ): RecipeRepository

}