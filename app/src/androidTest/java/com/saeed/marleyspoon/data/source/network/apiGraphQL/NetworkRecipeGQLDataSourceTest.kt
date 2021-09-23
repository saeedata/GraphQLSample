package com.saeed.marleyspoon.data.source.network.apiGraphQL

import com.apollographql.apollo3.api.ApolloResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.annotations.TestOnly
import java.util.*
import kotlin.collections.ArrayList

class NetworkRecipeGQLDataSourceTest : RecipeDataSource {

    @TestOnly
    override suspend fun getAllRecipes(
        skip: Int?,
        limit: Int?
    ): Flow<ApolloResponse<RecipeCollectionQuery.Data>> {

        val startIndex = (skip ?: 0)
        val endIndex = (skip ?: 0) + (limit ?: 10)

        val items = ArrayList<RecipeCollectionQuery.Item>()
        for (i in startIndex until endIndex) {

            items.add(
                RecipeCollectionQuery.Item(
                    sys = RecipeCollectionQuery.Sys(id = "$i"),
                    title = when (i) {
                        0 -> "Sample Recipe Without TagsCollection $i"
                        1 -> "Sample Recipe Without Chef $i"
                        2 -> "Sample Recipe Without Description $i"
                        else -> "Sample Recipe $i"
                    },
                    photo = RecipeCollectionQuery.Photo(url = "https://picsum.photos/id/$i/356/200")
                )
            )
        }

        return flowOf(
            ApolloResponse(
                requestUuid = UUID.randomUUID(),
                operation = RecipeCollectionQuery(skip, limit),
                data = RecipeCollectionQuery.Data(
                    RecipeCollectionQuery.RecipeCollection(
                        items = items,
                        skip = skip?:0,
                        limit = limit?:0,
                        total = 20
                    )
                )
            )
        )
    }

    @TestOnly
    override suspend fun getRecipeDetails(id: String): Flow<ApolloResponse<RecipeQuery.Data>> {
        return flowOf(
            when (id) {
                "0" -> {
                    // Sample Recipe Without TagsCollection
                    ApolloResponse(
                        requestUuid = UUID.randomUUID(),
                        operation = RecipeQuery(id),
                        data = RecipeQuery.Data(
                            RecipeQuery.Recipe(
                                title = "Sample Recipe Without TagsCollection",
                                photo = RecipeQuery.Photo(url = "https://picsum.photos/id/0/356/200"),
                                description = "This is a Sample Description",
                                chef = RecipeQuery.Chef(name = "Saeed Hashemi"),
                                tagsCollection = null
                            )
                        )
                    )
                }
                "1" -> {
                    // Sample Recipe Without Chef
                    ApolloResponse(
                        requestUuid = UUID.randomUUID(),
                        operation = RecipeQuery(id),
                        data = RecipeQuery.Data(
                            RecipeQuery.Recipe(
                                title = "Sample Recipe Without Chef",
                                photo = RecipeQuery.Photo(url = "https://picsum.photos/id/1/356/200"),
                                description = "This is a Sample Description",
                                chef = null,
                                tagsCollection = RecipeQuery.TagsCollection(
                                    items = listOf(
                                        RecipeQuery.Item(name = "vegan")
                                    )
                                )
                            )
                        )
                    )
                }
                "2" -> {
                    //  Sample Recipe Without Description
                    ApolloResponse(
                        requestUuid = UUID.randomUUID(),
                        operation = RecipeQuery(id),
                        data = RecipeQuery.Data(
                            RecipeQuery.Recipe(
                                title = "Sample Recipe Without Description",
                                photo = RecipeQuery.Photo(url = "https://picsum.photos/id/2/356/200"),
                                description = null,
                                chef = RecipeQuery.Chef(name = "Saeed Hashemi"),
                                tagsCollection = RecipeQuery.TagsCollection(
                                    items = listOf(
                                        RecipeQuery.Item(name = "vegan")
                                    )
                                )
                            )
                        )
                    )
                }
                else -> {
                    // Sample Recipe
                    ApolloResponse(
                        requestUuid = UUID.randomUUID(),
                        operation = RecipeQuery(id),
                        data = RecipeQuery.Data(
                            RecipeQuery.Recipe(
                                title = "Sample Recipe",
                                photo = RecipeQuery.Photo(url = "https://picsum.photos/$id/356/200"),
                                description = "This is a Sample Description",
                                chef = RecipeQuery.Chef(name = "Saeed Hashemi"),
                                tagsCollection = RecipeQuery.TagsCollection(
                                    items = listOf(
                                        RecipeQuery.Item(name = "vegan"),
                                        RecipeQuery.Item(name = "fatty"),
                                        RecipeQuery.Item(name = "healthy")
                                    )
                                )
                            )
                        )
                    )
                }
            }
        )
    }


}