package com.example.slide.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(
        var mDataList: MutableList<T> = mutableListOf(),
        var itemClickListener: ItemClickListener<T>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun add(itemList: List<T>) {
        val size = this.mDataList.size
        this.mDataList.addAll(itemList)
        val sizeNew = this.mDataList.size
        notifyItemRangeChanged(size, sizeNew)
    }

    fun addAt(position: Int, item: T) {
        mDataList.add(position, item)
        notifyDataSetChanged()
    }

    fun addAt(position: Int, itemList: List<T>) {
        val size = this.mDataList.size
        this.mDataList.addAll(position, itemList)
        val sizeNew = this.mDataList.size
        notifyItemRangeChanged(size, sizeNew)
    }

    fun setItemAt(position: Int, item: T) {
        mDataList[position] = item
        notifyItemChanged(position)
    }

    fun getItemAt(position: Int): T {
        return mDataList[position]
    }

    open fun set(dataList: List<T>) {
        val clone: List<T> = ArrayList(dataList)
        mDataList.clear()
        mDataList.addAll(clone)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mDataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun clear() {
        mDataList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mDataList.size

    interface ItemClickListener<T> {
        fun onItemClicked(view: View, item: T)
    }
}
