package com.example.slide.ui.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseRecyclerViewAdapter
import com.example.slide.database.entities.Draft
import com.example.slide.util.StringUtils
import kotlinx.android.synthetic.main.item_draft.view.*

class DraftAdapter(private val onDraftClicked: (draft: Draft) -> Unit,
                   private val onRenameClicked: (draft: Draft) -> Unit,
                   private val onCopyClicked: (draft: Draft) -> Unit,
                   private val onDeleteClicked: (draft: Draft) -> Unit) : BaseRecyclerViewAdapter<Draft>() {

    private inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var selectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_draft, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as DraftViewHolder
        val draft = mDataList[position]
        with(holder.itemView) {
            Glide.with(this).load(draft.images[0].url).into(imgPhotoDraft)
            tvDraftTitle.text = draft.title
            tvTotalDuration.text = StringUtils.getDurationDisplayFromSeconds(draft.totalDuration)
            setOnClickListener { onDraftClicked.invoke(draft) }
            btnMore.setOnClickListener {
                selectedIndex = position
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