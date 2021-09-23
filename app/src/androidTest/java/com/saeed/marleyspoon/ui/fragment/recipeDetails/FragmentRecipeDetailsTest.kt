package com.saeed.marleyspoon.ui.fragment.recipeDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.saeed.marleyspoon.R
import com.saeed.marleyspoon.domain.error.ErrorHandler
import com.saeed.marleyspoon.domain.model.BaseResponse
import com.saeed.marleyspoon.domain.model.network.response.RecipeQuery
import com.saeed.marleyspoon.domain.repository.RecipeRepository
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

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@MediumTest
class FragmentRecipeDetailsTest {

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

    lateinit var viewModel: FragmentRecipeDetailsViewModel

    @Suppress("UNCHECKED_CAST")
    inner class FragmentRecipeDetailsViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            (FragmentRecipeDetailsViewModel(errorHandler, recipeRepository) as T)
    }

    @Test
    fun test_display_pbLoading_while_calling_api() = runBlocking {

        val args = FragmentRecipeDetails.createArguments("0")

        val scenario = launchFragmentInHiltContainer<FragmentRecipeDetails>(
            fragmentArgs = args,
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeDetailsViewModelFactory()).get(
                FragmentRecipeDetailsViewModel::class.java
            )
        }

        // the first emit value should be Loading and pb_loading should be displayed
        val loading = viewModel.recipeDetailsUseCase.liveData.getOrAwaitValue()
        Truth.assertThat(loading).isEqualTo(BaseResponse.Loading)
        onView(withId(R.id.pb_loading)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))

        // wait until first page loaded successfully
        viewModel.recipeDetailsUseCase.liveData.getOrAwaitValueNotLoading()
        delay(100)
        onView(withId(R.id.pb_loading)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))

        scenario.close()
    }

//    sample fake date used for test cases

//    0 -> "Sample Recipe Without TagsCollection"
//    1 -> "Sample Recipe Without Chef"
//    2 -> "Sample Recipe Without Description"
//    else -> "Sample Recipe"

    @Test
    fun check_recipe_details_without_tags_collection() = runBlocking {

        val args = FragmentRecipeDetails.createArguments("0")

        val scenario = launchFragmentInHiltContainer<FragmentRecipeDetails>(
            fragmentArgs = args,
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeDetailsViewModelFactory()).get(
                FragmentRecipeDetailsViewModel::class.java
            )
        }

        // recipe details value fetch from live data
        val value = viewModel.recipeDetailsUseCase.liveData.getOrAwaitValueNotLoading()
        val response = (value as BaseResponse.Success)

        delay(100)

        response.data.data?.recipe?.let { recipe ->

            Truth.assertThat(recipe.photo?.url).isNotEmpty()
            onView(withId(R.id.img_recipe)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.title).isNotEmpty()
            onView(withId(R.id.txt_title)).check(matches(withText(recipe.title)))

            Truth.assertThat(recipe.chef?.name).isNotEmpty()
            onView(withId(R.id.txt_chef)).check(matches(withText(recipe.chef?.name)))
            onView(withId(R.id.txt_lbl_chef)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_chef)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.description).isNotEmpty()
            onView(withId(R.id.txt_description)).check(matches(withText(recipe.description)))
            onView(withId(R.id.txt_description)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_lbl_description)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.tagsCollection?.items).isAnyOf(null,emptyList<RecipeQuery.Item?>())
            onView(withId(R.id.txt_lbl_tags)).check(matches(not(isDisplayed())))
            onView(withId(R.id.chg_tags)).check(matches(not(isDisplayed())))

        }

        scenario.close()
    }

    @Test
    fun check_recipe_details_without_chef() = runBlocking {

        val args = FragmentRecipeDetails.createArguments("1")

        val scenario = launchFragmentInHiltContainer<FragmentRecipeDetails>(
            fragmentArgs = args,
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeDetailsViewModelFactory()).get(
                FragmentRecipeDetailsViewModel::class.java
            )
        }

        // recipe details value fetch from live data
        val value = viewModel.recipeDetailsUseCase.liveData.getOrAwaitValueNotLoading()
        val response = (value as BaseResponse.Success)

        delay(100)

        response.data.data?.recipe?.let { recipe ->

            Truth.assertThat(recipe.photo?.url).isNotEmpty()
            onView(withId(R.id.img_recipe)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.title).isNotEmpty()
            onView(withId(R.id.txt_title)).check(matches(withText(recipe.title)))

            Truth.assertThat(recipe.chef?.name).isAnyOf(null,"")
            onView(withId(R.id.txt_lbl_chef)).check(matches(not(isDisplayed())))
            onView(withId(R.id.txt_chef)).check(matches(not(isDisplayed())))

            Truth.assertThat(recipe.description).isNotEmpty()
            onView(withId(R.id.txt_description)).check(matches(withText(recipe.description)))
            onView(withId(R.id.txt_description)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_lbl_description)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.tagsCollection?.items).isNotEmpty()
            onView(withId(R.id.txt_lbl_tags)).check(matches(isDisplayed()))
            onView(withId(R.id.chg_tags)).check(matches(isDisplayed()))

        }

        scenario.close()
    }

    @Test
    fun check_recipe_details_without_description() = runBlocking {

        val args = FragmentRecipeDetails.createArguments("2")

        val scenario = launchFragmentInHiltContainer<FragmentRecipeDetails>(
            fragmentArgs = args,
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeDetailsViewModelFactory()).get(
                FragmentRecipeDetailsViewModel::class.java
            )
        }

        // recipe details value fetch from live data
        val value = viewModel.recipeDetailsUseCase.liveData.getOrAwaitValueNotLoading()
        val response = (value as BaseResponse.Success)

        delay(100)

        response.data.data?.recipe?.let { recipe ->

            Truth.assertThat(recipe.photo?.url).isNotEmpty()
            onView(withId(R.id.img_recipe)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.title).isNotEmpty()
            onView(withId(R.id.txt_title)).check(matches(withText(recipe.title)))

            Truth.assertThat(recipe.chef?.name).isNotEmpty()
            onView(withId(R.id.txt_chef)).check(matches(withText(recipe.chef?.name)))
            onView(withId(R.id.txt_lbl_chef)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_chef)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.description).isAnyOf(null,"")
            onView(withId(R.id.txt_description)).check(matches(not(isDisplayed())))
            onView(withId(R.id.txt_lbl_description)).check(matches(not(isDisplayed())))

            Truth.assertThat(recipe.tagsCollection?.items).isNotEmpty()
            onView(withId(R.id.txt_lbl_tags)).check(matches(isDisplayed()))
            onView(withId(R.id.chg_tags)).check(matches(isDisplayed()))

        }

        scenario.close()
    }

    @Test
    fun check_recipe_details_valid_all_fields() = runBlocking {

        val args = FragmentRecipeDetails.createArguments("3")

        val scenario = launchFragmentInHiltContainer<FragmentRecipeDetails>(
            fragmentArgs = args,
            navHostController = navController.testNavHostController
        ) {
            viewModel = ViewModelProvider(this, FragmentRecipeDetailsViewModelFactory()).get(
                FragmentRecipeDetailsViewModel::class.java
            )
        }

        // recipe details value fetch from live data
        val value = viewModel.recipeDetailsUseCase.liveData.getOrAwaitValueNotLoading()
        val response = (value as BaseResponse.Success)

        delay(100)

        response.data.data?.recipe?.let { recipe ->

            Truth.assertThat(recipe.photo?.url).isNotEmpty()
            onView(withId(R.id.img_recipe)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.title).isNotEmpty()
            onView(withId(R.id.txt_title)).check(matches(withText(recipe.title)))

            Truth.assertThat(recipe.chef?.name).isNotEmpty()
            onView(withId(R.id.txt_chef)).check(matches(withText(recipe.chef?.name)))
            onView(withId(R.id.txt_lbl_chef)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_chef)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.description).isNotEmpty()
            onView(withId(R.id.txt_description)).check(matches(withText(recipe.description)))
            onView(withId(R.id.txt_description)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_lbl_description)).check(matches(isDisplayed()))

            Truth.assertThat(recipe.tagsCollection?.items).isNotEmpty()
            onView(withId(R.id.txt_lbl_tags)).check(matches(isDisplayed()))
            onView(withId(R.id.chg_tags)).check(matches(isDisplayed()))

        }

        scenario.close()
    }
}