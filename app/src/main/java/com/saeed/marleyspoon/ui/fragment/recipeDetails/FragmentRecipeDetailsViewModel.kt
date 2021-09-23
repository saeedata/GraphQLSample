package com.saeed.marleyspoon.ui.fragment.recipeDetails

import androidx.lifecycle.ViewModel
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.repository.RecipeRepository
import com.saeed.marleyspoon.domain.usecase.SimpleApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentRecipeDetailsViewModel @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    lateinit var recipeId:String

    val recipeDetailsUseCase by lazy {
        SimpleApiUseCase(
            errorHandler = errorHandler
        ) {
            recipeRepository.getRecipeDetails(recipeId)
        }
    }

}