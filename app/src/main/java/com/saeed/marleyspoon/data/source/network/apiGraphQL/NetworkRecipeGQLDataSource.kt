package com.saeed.marleyspoon.data.source.network.apiGraphQL

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.saeed.marleyspoon.di.NetworkModule
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NetworkRecipeGQLDataSource @Inject constructor(
    @NetworkModule.ContentFulGraphQL val client: ApolloClient
) : RecipeDataSource {

    override suspend fun getAllRecipes(
        skip: Int?,
        limit: Int?
    ): Flow<ApolloResponse<RecipeCollectionQuery.Data>> {
        return client.queryAsFlow(
            ApolloRequest(
                RecipeCollectionQuery(
                    skip = skip,
                    limit = limit
                )
            )
        )
    }

    override suspend fun getRecipeDetails(id: String): Flow<ApolloResponse<RecipeQuery.Data>> {
        return client.queryAsFlow(
            ApolloRequest(
                RecipeQuery(
                    id = id
                )
            )
        )
    }

}