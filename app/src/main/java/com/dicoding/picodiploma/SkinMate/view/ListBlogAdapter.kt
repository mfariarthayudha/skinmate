package com.dicoding.picodiploma.SkinMate.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.SkinMate.R
import com.dicoding.picodiploma.SkinMate.model.DataBlog

class ListBlogAdapter (private val listBlog: ArrayList<DataBlog>) : RecyclerView.Adapter<ListBlogAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_blog, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo) = listBlog[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description


        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Selamat membaca tentang " + listBlog[holder.adapterPosition].title, Toast.LENGTH_SHORT).show()

            val openURL = Intent(android.content.Intent.ACTION_VIEW)

            openURL.data = Uri.parse("https://cnfstore.com/blog/post/baca-5-tips-menjaga-kesehatan-kulit-mudah-dan-sederhana")
//            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            openURL.putExtra("key_president", listBlog[holder.adapterPosition])

            holder.itemView.context.startActivity(openURL, ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.context as Activity).toBundle())
        }

    }

    override fun getItemCount(): Int = listBlog.size
}


