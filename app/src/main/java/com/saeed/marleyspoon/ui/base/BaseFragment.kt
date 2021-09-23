package com.saeed.marleyspoon.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    private var requestCode: Int = -100
    var launchFragmentForResult: ((resultCode: Int, data: Intent?, requestCode: Int) -> Unit)? =
        null
    lateinit var binding: T

    @LayoutRes
    abstract fun getLayoutRes(): Int
    private var startFragmentResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            launchFragmentForResult?.invoke(result.resultCode, result.data, requestCode)
        }

    fun startFragmentForResult(requestCode: Int, intent: Intent) {
        this.requestCode = requestCode
        startFragmentResult.launch(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, getLayoutRes(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
    }

}