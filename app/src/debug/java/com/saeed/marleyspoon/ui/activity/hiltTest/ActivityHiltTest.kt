package com.saeed.marleyspoon.ui.activity.hiltTest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.saeed.marleyspoon.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ActivityHiltTest : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(
            intent.getIntExtra(
                EXTRA_THEME_BUNDLE_KEY,
                R.style.FragmentScenarioEmptyFragmentActivityTheme
            )
        )
        super.onCreate(savedInstanceState)
    }


    companion object {

        const val EXTRA_THEME_BUNDLE_KEY = ".EXTRA.THEME_BUNDLE_KEY"
    }
}
