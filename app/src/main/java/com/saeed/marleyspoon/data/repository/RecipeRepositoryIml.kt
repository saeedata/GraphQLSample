package com.saeed.marleyspoon.data.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.model.BaseResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import com.saeed.marleyspoon.domain.repository.RecipeRepository
import com.saeed.marleyspoon.data.source.network.apiGraphQL.RecipeDataSource
import com.saeed.marleyspoon.utils.extention.toResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeRepositoryIml @Inject constructor(
    private val dataSource: RecipeDataSource,
    private val errorHandler: ErrorHandler
) : RecipeRepository {

    override suspend fun getAllRecipes(
        skip: Int?,
        limit: Int?
    ): Flow<BaseResponse<ApolloResponse<RecipeCollectionQuery.Data>>> {
        return dataSource.getAllRecipes(skip, limit).toResult(errorHandler)
    }

    override suspend fun getRecipeDetails(id: String): Flow<BaseResponse<ApolloResponse<RecipeQuery.Data>>> {
        return dataSource.getRecipeDetails(id).toResult(errorHandler)
    }


}