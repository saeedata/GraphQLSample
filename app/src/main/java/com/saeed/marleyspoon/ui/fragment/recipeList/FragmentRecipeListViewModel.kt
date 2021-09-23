package com.saeed.marleyspoon.ui.fragment.recipeList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.repository.RecipeRepository
import com.saeed.marleyspoon.domain.usecase.PagingApiUseCase
import com.saeed.marleyspoon.ui.fragment.recipeList.FragmentRecipeList.Companion.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentRecipeListViewModel @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    val recipeListUseCase by lazy {
        PagingApiUseCase(
            skipInitValue = 0,
            limitInitValue = PAGE_SIZE,
            errorHandler = errorHandler
        ) { skip, limit ->
            recipeRepository.getAllRecipes(skip, limit)
        }
    }

}