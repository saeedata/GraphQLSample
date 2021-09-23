package com.saeed.marleyspoon.domain.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.saeed.marleyspoon.domain.model.BaseResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun getAllRecipes(
        skip: Int? = null,
        limit: Int? = null
    ): Flow<BaseResponse<ApolloResponse<RecipeCollectionQuery.Data>>>

    suspend fun getRecipeDetails(id: String): Flow<BaseResponse<ApolloResponse<RecipeQuery.Data>>>
}