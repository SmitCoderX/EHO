package com.driver.eho.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.driver.eho.R


class HorizontalRecyclerView(private var uri: List<Uri>) :
    RecyclerView.Adapter<HorizontalRecyclerView.HorizontalViewHolder>() {
    private val limit = 5

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): HorizontalViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_document, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(horizontalViewHolder: HorizontalViewHolder, position: Int) {
        /*horizontalViewHolder.mImageRecyclerView.setImageURI(uri[position])*/
        /*Picasso.get().load(uri[position]).into(horizontalViewHolder.mImageRecyclerView)*/
        Glide.with(horizontalViewHolder.itemView)
            .load(uri[position])
            .into(horizontalViewHolder.mImageRecyclerView)
    }

    override fun getItemCount(): Int {
        return if (uri.size > limit) {
            limit
        } else {
            uri.size
        }
    }

    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageRecyclerView: ImageView = itemView.findViewById(R.id.ivDocument)
    }

    fun updateData(list: List<Uri>) {
        uri = list
        notifyDataSetChanged()
    }
}