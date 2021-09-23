package com.saeed.marleyspoon.ui.fragment.recipeList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.saeed.marleyspoon.R
import com.saeed.marleyspoon.databinding.ItemRecipeListBinding
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.model.BaseResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeCollectionQuery
import com.saeed.marleyspoon.domain.repository.RecipeRepository
import com.saeed.marleyspoon.ui.adapter.myBindingAdapter.MyBindingViewHolder
import com.saeed.marleyspoon.utils.TestNavHostControllerRule
import com.saeed.marleyspoon.utils.extention.getOrAwaitValue
import com.saeed.marleyspoon.utils.extention.getOrAwaitValueNotLoading
import com.saeed.marleyspoon.utils.extention.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class FragmentRecipeListTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val navController = TestNavHostControllerRule(
        navigationGraph = R.navigation.main_nav,
    )

    @Inject
    lateinit var errorHandler: ErrorHandler

    @Inject
    lateinit var recipeRepository: RecipeRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
    }

    lateinit var viewModel: FragmentRecipeListViewModel

    inner class FragmentRecipeListViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            (FragmentRecipeListViewModel(errorHandler, recipeRepository) as T)
    }

    @Test
    fun check_current_fragment_is_FragmentRecipeList() {

        val scenario = launchFragmentInHiltContainer<FragmentRecipeList>(
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeListViewModelFactory()).get(
                FragmentRecipeListViewModel::class.java
            )
        }

        scenario.onActivity {

            // assert that currentDestination is FragmentRecipeList
            Truth.assertThat(navController.testNavHostController.currentDestination?.id)
                .isEqualTo(R.id.recipeList)
        }

        scenario.close()
    }

    @Test
    fun test_display_pbLoading_while_calling_api() =  runBlocking {

        val scenario = launchFragmentInHiltContainer<FragmentRecipeList>(
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeListViewModelFactory()).get(
                FragmentRecipeListViewModel::class.java
            )
        }

        // the first emit value should be Loading and pb_loading should be displayed
        val loading = viewModel.recipeListUseCase.liveData.getOrAwaitValue()
        Truth.assertThat(loading).isEqualTo(BaseResponse.Loading)
        onView(withId(R.id.pb_loading)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))

        // wait until first page loaded successfully
        viewModel.recipeListUseCase.liveData.getOrAwaitValueNotLoading()
        delay(100)
        onView(withId(R.id.pb_loading)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))

        scenario.close()
    }

    @Test
    fun test_pagination_on_scroll_to_end_of_list() = runBlocking {

        val scenario = launchFragmentInHiltContainer<FragmentRecipeList>(
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeListViewModelFactory()).get(
                FragmentRecipeListViewModel::class.java
            )
        }

        // wait until first page loaded successfully
        val firstPage = viewModel.recipeListUseCase.liveData.getOrAwaitValueNotLoading()
        val firstResponse = (firstPage as BaseResponse.Success)
        Truth.assertThat(firstResponse.data.data?.recipeCollection?.skip).isEqualTo(0)

        // scroll to end of list
        onView(withId(R.id.rv_list))
            .perform(
                RecyclerViewActions
                    .scrollToPosition<MyBindingViewHolder<RecipeCollectionQuery.Item, ItemRecipeListBinding>>(
                        9
                    )
            )

        // the first emit value should be Loading and pb_pagination should be displayed
        val firstLoading = viewModel.recipeListUseCase.liveData.getOrAwaitValue()
        Truth.assertThat(firstLoading).isEqualTo(BaseResponse.Loading)
        onView(withId(R.id.pb_pagination)).check(matches(isDisplayed()))
        onView(withId(R.id.pb_loading)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))

        // wait until next page loaded successfully
        val secondPage = viewModel.recipeListUseCase.liveData.getOrAwaitValueNotLoading()
        val secondResponse = (secondPage as BaseResponse.Success)
        Truth.assertThat(secondResponse.data.data?.recipeCollection?.skip).isEqualTo(10)


        // we had 2 pages in fake data source, so should not load more if scroll to end of list again
        // scroll to top of list
        onView(withId(R.id.rv_list))
            .perform(
                RecyclerViewActions
                    .scrollToPosition<MyBindingViewHolder<RecipeCollectionQuery.Item, ItemRecipeListBinding>>(
                        0
                    )
            )

        delay(300)

        // scroll to end of list
        onView(withId(R.id.rv_list))
            .perform(
                RecyclerViewActions
                    .scrollToPosition<MyBindingViewHolder<RecipeCollectionQuery.Item, ItemRecipeListBinding>>(
                        0
                    )
            )

        // the last value should be the same as before
        val lastValue = viewModel.recipeListUseCase.liveData.getOrAwaitValue()
        Truth.assertThat(lastValue).isNotEqualTo(BaseResponse.Loading)
        onView(withId(R.id.pb_pagination)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pb_loading)).check(matches(not(isDisplayed())))
        val lastResponse = (lastValue as BaseResponse.Success)
        Truth.assertThat(lastResponse.data.data?.recipeCollection?.skip).isEqualTo(10)

        scenario.close()
    }

}
