package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewEmptySupport : RecyclerView {
    private lateinit var emptyView: View
    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            showEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyView()
        }
    }

    constructor(context: Context?) : super(context!!)

    fun showEmptyView() {
        adapter?.let { adapter ->
            if (adapter.itemCount == 0) {
                emptyView.visibility = VISIBLE
                this@RecyclerViewEmptySupport.visibility = GONE
            } else {
                emptyView.visibility = GONE
                this@RecyclerViewEmptySupport.visibility = VISIBLE
            }
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.let { adapter ->
            adapter.registerAdapterDataObserver(observer)
            observer.onChanged()
        }
    }

    fun setEmptyView(v: View) {
        emptyView = v
    }
}
