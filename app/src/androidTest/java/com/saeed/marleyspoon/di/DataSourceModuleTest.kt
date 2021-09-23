package com.saeed.marleyspoon.di

import com.saeed.marleyspoon.data.source.network.apiGraphQL.NetworkRecipeGQLDataSourceTest
import com.saeed.marleyspoon.data.source.network.apiGraphQL.RecipeDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataSourceModule::class]
)
 class DataSourceModuleTest {

    @Provides
    @Singleton
     fun bindNetworkRecipeGQLDataSource(): RecipeDataSource {
         return NetworkRecipeGQLDataSourceTest()
     }

}
