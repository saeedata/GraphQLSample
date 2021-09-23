package com.saeed.marleyspoon.ui.adapter.myBindingAdapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class MyBindingViewHolder<M, B : ViewDataBinding>(
    val binder: B,
    val onBindViewHolder: (
        item: M,
        position: Int,
        binder: B
    ) -> Unit
) : RecyclerView.ViewHolder(binder.root)