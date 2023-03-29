package com.example.slide.ui.my_studio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseRecyclerViewAdapter
import com.example.slide.database.entities.Draft

class DraftAdapter(private val onDraftClicked: (draft: Draft) -> Unit,
                   private val onRenameClicked: (draft: Draft) -> Unit,
                   private val onCopyClicked: (draft: Draft) -> Unit,
                   private val onDeleteClicked: (draft: Draft) -> Unit) :
        BaseRecyclerViewAdapter<Draft>() {

    private inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as DraftViewHolder
        val draft = mDataList[position]
        with(holder.itemView) {
            Glide.with(this).load(draft.images[0].url).into(findViewById(R.id.iv_thumb))
            findViewById<TextView>(R.id.tv_duration).text = draft.title
            setOnClickListener { onDraftClicked.invoke(draft) }
            findViewById<View>(R.id.btn_more).setOnClickListener {
                openMenu(it, draft)
            }
        }
    }

    private fun openMenu(v: View, draft: Draft) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.draft_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_rename -> {
                    onRenameClicked.invoke(draft)
                }
                R.id.menu_copy -> {
                    onCopyClicked.invoke(draft)
                }
                R.id.menu_delete -> {
                    onDeleteClicked.invoke(draft)
                }
            }
            true
        }
        popup.show()
    }
}