package com.saeed.marleyspoon.ui.fragment.recipeDetails

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.saeed.marleyspoon.R
import com.saeed.marleyspoon.databinding.FragmentRecipeDetailsBinding
import com.saeed.marleyspoon.domain.model.BaseError
import com.saeed.marleyspoon.domain.model.BaseResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import com.saeed.marleyspoon.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class FragmentRecipeDetails : BaseFragment<FragmentRecipeDetailsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_recipe_details

    private val viewModel: FragmentRecipeDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSharedElementTransition()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        postponeEnterTransition()

        loadArguments()

        startPostponedEnterTransition()

        super.onViewCreated(view, savedInstanceState)

        initViews()

        observeData()
    }

    private fun setupSharedElementTransition() {
        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    private fun loadArguments() {
        arguments?.apply {

            binding.cvHeader.transitionName = getString(EXTRA_TRANSITION_NAME)

            viewModel.recipeId = getString(EXTRA_RECIPE_ID, "")

            val recipeTitle = getString(EXTRA_RECIPE_TITLE)
            val recipePhotoUrl = getString(EXTRA_RECIPE_PHOTO_URL)

            bindPreviewData(recipeTitle, recipePhotoUrl)
        }
    }

    private fun bindPreviewData(recipeTitle: String?, recipePhotoUrl: String?) {

        binding.recipe = RecipeQuery.Recipe(
            title = recipeTitle,
            photo = RecipeQuery.Photo(
                url = recipePhotoUrl
            ),
            null,
            null,
            null
        )

    }

    private fun initViews() {

    }

    private fun initTags(tags: List<RecipeQuery.Item?>?) {

        tags?.forEach { tag ->
            tag?.name?.also {
                val tagView = Chip(requireContext(), null, R.attr.tagChip).apply {
                    text = tag.name
                    isEnabled = false
                }
                binding.chgTags.addView(tagView)
            }

        }
    }

    private fun observeData() {
        viewModel.recipeDetailsUseCase.liveData.observe(viewLifecycleOwner, {
            when (it) {
                is BaseResponse.Loading -> onLoading(true)
                is BaseResponse.Success -> onDataLoadSuccessfully(it.data.data)
                is BaseResponse.Error -> onDataLoadFailed(it.error)
            }
        })
    }

    private fun onLoading(isLoading: Boolean) {
        binding.isLoading = isLoading
        onError(false)
    }


    private fun onError(hasError: Boolean) {
        binding.layoutError.root.isVisible = hasError
    }


    private fun onDataLoadFailed(error: BaseError) {

        Timber.e(error.toString())

        onLoading(false)
        onError(true)

        binding.layoutError.btnTryAgain.setOnClickListener {
            onError(false)
            viewModel.recipeDetailsUseCase.retry()
        }
    }


    private fun onDataLoadSuccessfully(data: RecipeQuery.Data?) {
        onLoading(false)
        onError(false)
        data?.recipe?.also { recipe ->
            binding.recipe = recipe
            binding.isTagsEmpty = recipe.tagsCollection?.items.isNullOrEmpty()
            initTags(recipe.tagsCollection?.items)
        } ?: run {
            binding.isTagsEmpty = true
        }
    }

    companion object {

        const val EXTRA_RECIPE_ID = ".EXTRA.RECIPE_ID"
        const val EXTRA_RECIPE_TITLE = ".EXTRA.RECIPE_TITLE"
        const val EXTRA_RECIPE_PHOTO_URL = ".EXTRA.RECIPE_PHOTO_URL"

        const val EXTRA_TRANSITION_NAME = ".EXTRA.TRANSITION_NAME"

        fun getInstance(args: Bundle?): FragmentRecipeDetails {
            return FragmentRecipeDetails().apply {
                arguments = args
            }
        }

        fun createArguments(
            recipeId: String,
            title: String? = null,
            photoUrl: String? = null,
            transition: String? = null
        ): Bundle {
            return bundleOf(
                EXTRA_RECIPE_ID to recipeId,
                EXTRA_RECIPE_TITLE to title,
                EXTRA_RECIPE_PHOTO_URL to photoUrl,
                EXTRA_TRANSITION_NAME to transition
            )
        }
    }
}