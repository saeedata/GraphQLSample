package com.saeed.marleyspoon.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

abstract class EndlessRecyclerOnScrollListener : RecyclerView.OnScrollListener() {

    private var loading = true
    private var previousItemCount = 0

    var currentPage = 1
    var totalPages = 0


    fun refresh() {
        currentPage = 1
        totalPages = 0
        previousItemCount = 0
        loading = true
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = when {
            (recyclerView.layoutManager is LinearLayoutManager) -> recyclerView.layoutManager as LinearLayoutManager
            (recyclerView.layoutManager is GridLayoutManager) -> recyclerView.layoutManager as GridLayoutManager
            else -> throw IllegalArgumentException("unknown layout manager")
        }
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        if (lastPosition == recyclerView.adapter!!.itemCount - 1 && !loading) {
            if (currentPage < totalPages){
                currentPage++
                onLoadMore(currentPage)
                loading = true
            }
        }
        if (recyclerView.adapter!!.itemCount > previousItemCount){
            previousItemCount = recyclerView.adapter!!.itemCount
            loading = false

        }
    }

    abstract fun onLoadMore(currentPage: Int)

}