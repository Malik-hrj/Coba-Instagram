package com.example.cobainstagram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cobainstagram.R
import com.example.cobainstagram.model.Post

class MyImagesAdapter(private val mContext: Context, private val mPost: List<Post>) :
    RecyclerView.Adapter<MyImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_post, parent, false)
        return MyImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val mPost = mPost[position]


        Glide.with(mContext).load(mPost.getPostImage()).into(holder.postImageGrid)
    }

}

class MyImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    var postImageGrid : ImageView = itemView.findViewById(R.id.post_image_grid)

}
