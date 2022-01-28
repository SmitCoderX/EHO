package com.driver.eho.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.driver.eho.R


class HorizontalRecyclerView(uri: ArrayList<Uri>) :
    RecyclerView.Adapter<HorizontalRecyclerView.HorizontalViewHolder>() {
    private val limit = 5
    private val uri: ArrayList<Uri>

    init {
        this.uri = uri
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): HorizontalViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_all_document,parent,false)
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
        if(uri.size > limit){ return limit }
        else { return uri.size }
    }

    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageRecyclerView: ImageView
        init {
            mImageRecyclerView = itemView.findViewById(R.id.ivDocument)
        }
    }
}