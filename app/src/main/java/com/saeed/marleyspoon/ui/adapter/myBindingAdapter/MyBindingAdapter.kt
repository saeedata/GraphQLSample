package com.saeed.marleyspoon.ui.adapter.myBindingAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


class MyBindingAdapter<M, B : ViewDataBinding>(
    private val context: Context?,
    @LayoutRes val layout: Int? = null,
    @LayoutRes val layouts: IntArray? = null,
    val onCreateViewHolder: ((
        binder: B
    ) -> Unit)? = null,
    val onBindViewHolder: (
        item: M,
        position: Int,
        binder: B
    ) -> Unit,
    diffCallback: DiffUtil.ItemCallback<M>
) : ListAdapter<M, MyBindingViewHolder<M, B>>(
    AsyncDifferConfig.Builder(diffCallback).build()
) {

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItem(position: Int): M {
        return currentList[position]
    }

    override fun getItemViewType(position: Int): Int {
        return (currentList[position] as? MyBindingViewType)?.itemViewType ?: 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyBindingViewHolder<M, B> {

        val layoutId = when {
            layout != null -> layout
            layouts != null -> layouts[viewType]
            else -> throw IllegalArgumentException("unknown layout id")
        }
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(context),
            layoutId,
            parent,
            false
        )
        onCreateViewHolder?.invoke(binding)
        return MyBindingViewHolder(binding, onBindViewHolder)
    }

    override fun onBindViewHolder(holder: MyBindingViewHolder<M, B>, position: Int) {
        holder.onBindViewHolder.invoke(getItem(position), position, holder.binder)
    }

}
