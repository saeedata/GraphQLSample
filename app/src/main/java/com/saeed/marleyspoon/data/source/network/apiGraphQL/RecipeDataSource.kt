package com.saeed.marleyspoon.data.source.network.apiGraphQL

import com.apollographql.apollo3.api.ApolloResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import kotlinx.coroutines.flow.Flow

interface RecipeDataSource {

    suspend fun getAllRecipes(skip: Int?=null, limit: Int?=null) : Flow<ApolloResponse<RecipeCollectionQuery.Data>>

    suspend fun getRecipeDetails(id: String) : Flow<ApolloResponse<RecipeQuery.Data>>

}