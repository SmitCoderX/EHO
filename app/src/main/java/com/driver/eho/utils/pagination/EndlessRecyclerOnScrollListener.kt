package com.driver.eho.utils.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerOnScrollListener(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = true // True if we are still waiting for the last set of data to load.
    private val visibleThreshold =
        10 // The minimum amount of items to have below your current scroll position before loading more.
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private val startingPageIndex = 1
    private var current_page = 1
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) //check for scroll down
        {
            visibleItemCount = recyclerView.childCount
            totalItemCount = mLinearLayoutManager.itemCount
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            }
            if (!loading && totalItemCount - visibleItemCount
                <= firstVisibleItem + visibleThreshold
            ) {
                // End has been reached

                // Do something
                current_page++
                onLoadMore(current_page)
                loading = true
            }
        } else if (mLinearLayoutManager.findFirstVisibleItemPosition() < 4) {
//            onShowTop(false);
        }
    }

    abstract fun onLoadMore(current_page: Int)

    // Call whenever performing new searches
    fun resetState() {
        current_page = startingPageIndex
        previousTotal = 0
        loading = true
    }

    fun previousState() {
        current_page = current_page - 1
        previousTotal = 0
        loading = true
    }

    companion object {
        var TAG = EndlessRecyclerOnScrollListener::class.java.simpleName
    }
}