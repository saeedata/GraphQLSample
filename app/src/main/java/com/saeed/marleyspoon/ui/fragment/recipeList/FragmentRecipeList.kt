package com.saeed.marleyspoon.ui.fragment.recipeList

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import com.saeed.marleyspoon.R
import com.saeed.marleyspoon.databinding.FragmentRecipeListBinding
import com.saeed.marleyspoon.databinding.ItemRecipeListBinding
import com.saeed.marleyspoon.domain.model.BaseError
import com.saeed.marleyspoon.domain.model.BaseResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.ui.adapter.myBindingAdapter.MyBindingAdapter
import com.saeed.marleyspoon.ui.base.BaseFragment
import com.saeed.marleyspoon.utils.EndlessRecyclerOnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class FragmentRecipeList : BaseFragment<FragmentRecipeListBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_recipe_list

    private val viewModel: FragmentRecipeListViewModel by viewModels()

    private lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener
    private lateinit var recipeListAdapter: MyBindingAdapter<RecipeCollectionQuery.Item, ItemRecipeListBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        observeData()
    }


    private fun initViews() {

        initRecyclerView()
    }

    private fun initRecyclerView() {
        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore(currentPage: Int) {
                viewModel.recipeListUseCase.nextPage((PAGE_SIZE * (currentPage - 1)), PAGE_SIZE)
            }
        }

        recipeListAdapter = MyBindingAdapter(
            context = requireContext(),
            layout = R.layout.item_recipe_list,
            onCreateViewHolder = { binder ->
                binder.cvRecipe.setOnClickListener {
                    binder.recipeItem?.apply {


                        val extras = FragmentNavigatorExtras(
                            binder.cvRecipe to binder.cvRecipe.transitionName
                        )

                        val direction =
                            FragmentRecipeListDirections.actionRecipeListToRecipeDetails(
                                sys.id,
                                title,
                                photo?.url,
                                binder.cvRecipe.transitionName
                            )

                        it.findNavController().navigate(direction, extras)
                    }
                }
            },
            onBindViewHolder = { item, _, binder ->
                binder.recipeItem = item
                binder.cvRecipe.transitionName = "cvRecipe-${binder.recipeItem?.sys?.id ?: -1}"
            },
            diffCallback = object : DiffUtil.ItemCallback<RecipeCollectionQuery.Item>() {
                override fun areItemsTheSame(
                    oldItem: RecipeCollectionQuery.Item,
                    newItem: RecipeCollectionQuery.Item
                ): Boolean {
                    return oldItem.sys.id == newItem.sys.id
                }

                override fun areContentsTheSame(
                    oldItem: RecipeCollectionQuery.Item,
                    newItem: RecipeCollectionQuery.Item
                ): Boolean {
                    return oldItem.title == newItem.title && oldItem.photo?.url == newItem.photo?.url
                }
            }
        )
        binding.rvList.apply {
            adapter = recipeListAdapter
            addOnScrollListener(endlessRecyclerOnScrollListener)

        }
    }

    private fun observeData() {
        viewModel.recipeListUseCase.liveData.observe(viewLifecycleOwner, {
            when (it) {
                is BaseResponse.Loading -> if (endlessRecyclerOnScrollListener.currentPage == 1) {
                    onLoading(true)
                } else {
                    onPaginationLoading(true)
                }
                is BaseResponse.Success -> onDataLoadSuccessfully(it.data.data)
                is BaseResponse.Error -> onDataLoadFailed(it.error)
            }
        })
    }

    private fun onLoading(isLoading: Boolean) {
        binding.isLoading = isLoading
        onError(false)
    }

    private fun onPaginationLoading(isLoadingPagination: Boolean) {
        binding.isLoadingPagination = isLoadingPagination
    }

    private fun onError(hasError: Boolean) {
        binding.layoutError.root.isVisible = hasError
    }

    private fun onDataLoadFailed(error: BaseError) {

        Timber.e(error.toString())

        onLoading(false)
        onPaginationLoading(false)
        onError(true)

        binding.layoutError.btnTryAgain.setOnClickListener {
            onError(false)
            viewModel.recipeListUseCase.retry()
        }
    }

    private fun onDataLoadSuccessfully(data: RecipeCollectionQuery.Data?) {

        onLoading(false)
        onPaginationLoading(false)
        onError(false)
        data?.recipeCollection?.apply {
            endlessRecyclerOnScrollListener.totalPages = total / PAGE_SIZE
            recipeListAdapter.submitList(items)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}